package com.muhammad.camera

import android.annotation.SuppressLint
import android.app.Application
import android.media.Image
import android.media.MediaActionSound
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageInfo
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.takePicture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.application
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.work.await
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.muhammad.util.LocalFileProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraViewModel(
     application: Application,
    val localFileProvider: LocalFileProvider,
    val rearCameraUseCase: RearCameraUseCase,
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()
    private val _foldingFeature = MutableStateFlow<FoldingFeature?>(null)
    val foldingFeature = _foldingFeature.asStateFlow()
    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null
    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _state.update { it.copy(surfaceRequest = newSurfaceRequest) }
            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                newSurfaceRequest.resolution.width.toFloat(),
                newSurfaceRequest.resolution.height.toFloat()
            )
        }
    }
    private val cameraCaptureUseCase = ImageCapture.Builder().build()
    private val cameraImageAnalysisUseCase =
        ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    private val cameraUseCaseGroup =
        UseCaseGroup.Builder().addUseCase(cameraPreviewUseCase).addUseCase(cameraCaptureUseCase)
            .addUseCase(cameraImageAnalysisUseCase).build()
    private val cameraTypeFlow = MutableStateFlow<CameraSelector?>(null)
    private var mediaActionSound: MediaActionSound? = null
    private var autoFocusRequestId: Int = 0
    suspend fun bindToCamera() = suspendCancellableCoroutine {cont ->
        val job = viewModelScope.launch {
            launch { runPostDetection() }
            val processCameraProvider = ProcessCameraProvider.awaitInstance(application)
            val availableCameraLenses = listOf(
                DEFAULT_BACK_CAMERA,
                DEFAULT_FRONT_CAMERA
            ).filter {
                processCameraProvider.hasCamera(it)
            }
            _state.update { it.copy(canFlipCamera = availableCameraLenses.size == 2) }
            cameraTypeFlow.update { it ?: availableCameraLenses.firstOrNull() }
            mediaActionSound = MediaActionSound()
            cameraTypeFlow.onCompletion {
                mediaActionSound?.release()
                mediaActionSound = null
                cameraControl = null
                cameraInfo = null
            }.collectLatest { cameraType ->
                autoFocusRequestId++
                _state.update { it.copy(autoFocusUIState = AutoFocusUIState.Unspecified) }
                if (cameraType != null) {
                    processCameraProvider.runWith(cameraType, cameraUseCaseGroup) { camera ->
                        cameraControl = camera.cameraControl
                        cameraInfo = camera.cameraInfo
                        _state.update { it.copy(cameraSessionId = it.cameraSessionId + 1) }
                        camera.cameraInfo.zoomState.asFlow().collectLatest { zoomState ->
                            _state.update {
                                it.copy(
                                    zoomLevel = zoomState.zoomRatio,
                                    zoomMinRatio = zoomState.minZoomRatio,
                                    zoomMaxRatio = zoomState.maxZoomRatio
                                )
                            }
                        }
                    }
                }
            }
        }
        job.invokeOnCompletion {cause ->
            if(cause == null){
                cont.resume(Unit)
            } else{
                cont.resumeWithException(cause)
            }
        }
        cont.invokeOnCancellation { job.cancel() }
    }

    fun setCaptureImage(uri: Uri?) {
        _state.update { it.copy(imageUri = uri) }
    }

    fun captureImage() {
        viewModelScope.launch {
            mediaActionSound?.play(MediaActionSound.SHUTTER_CLICK)
            val file = localFileProvider.getFileFromCache("image${System.currentTimeMillis()}.jpg")
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).apply {
                if (cameraTypeFlow.value == DEFAULT_FRONT_CAMERA) {
                    val metadata = ImageCapture.Metadata().apply {
                        isReversedHorizontal = true
                    }
                    setMetadata(metadata)
                }
            }.build()
            try {
                val outputFileResults = cameraCaptureUseCase.takePicture(outputFileOptions)
                _state.update { it.copy(imageUri = outputFileResults.savedUri) }
            } catch (e: ImageCaptureException) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun tapToFocus(tapCoordintes: Offset) {
        viewModelScope.launch {
            val requestId = ++autoFocusRequestId
            val point = surfaceMeteringPointFactory?.createPoint(tapCoordintes.x, tapCoordintes.y)
            if (point != null) {
                _state.update {
                    it.copy(
                        autoFocusUIState = AutoFocusUIState.Specified(
                            surfaceCoordinates = tapCoordintes,
                            status = AutoFocusUIState.Status.RUNNING
                        )
                    )
                }
            }
            val meteringAction = FocusMeteringAction.Builder(point ?: return@launch).build()
            val completionAction = try {
                if (cameraControl?.startFocusAndMetering(meteringAction)
                        ?.await()?.isFocusSuccessful == true
                ) {
                    mediaActionSound?.play(MediaActionSound.FOCUS_COMPLETE)
                    AutoFocusUIState.Status.SUCCESS
                } else {
                    AutoFocusUIState.Status.FAILURE
                }
            } catch (e: CameraControl.OperationCanceledException) {
                e.printStackTrace()
                AutoFocusUIState.Status.CANCELLED
            }
            if (requestId == autoFocusRequestId) {
                _state.update {
                    it.copy(
                        autoFocusUIState = AutoFocusUIState.Specified(
                            surfaceCoordinates = tapCoordintes,
                            status = completionAction
                        )
                    )
                }
            }
        }
    }

    fun setZoomLevel(zoomLevel: Float) {
        viewModelScope.launch {
            cameraControl?.apply {
                cancelFocusAndMetering()
                setZoomRatio(zoomLevel)
            }
        }
    }

    fun flipCameraDirection() {
        cameraTypeFlow.update {
            if (it == DEFAULT_BACK_CAMERA) DEFAULT_FRONT_CAMERA else DEFAULT_BACK_CAMERA
        }
    }

    fun initRearDisplayFeature(activity: ComponentActivity) {
        rearCameraUseCase.init(activity)
    }

    fun shouldShowRearDisplayFeature() = rearCameraUseCase.shouldDisplayRearCameraButton()
    fun toggleRearDisplayFeature(activity: ComponentActivity) {
        rearCameraUseCase.toggleRearCameraDisplay(activity)
        _state.update { it.copy(isRearCameraActive = rearCameraUseCase.isRearCameraActive()) }
    }

    fun calculateFoldingFeature(activity: ComponentActivity) {
        val job = viewModelScope.launch {
            WindowInfoTracker.getOrCreate(activity).windowLayoutInfo(activity).collect { info ->
                _foldingFeature.update {
                    info.displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
                }
            }
        }
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                job.cancel()
            }
        })
    }

    private suspend fun PoseDetector.detectPersonInFrame(
        image: Image,
        imageInfo: ImageInfo,
    ): Boolean {
        val results = process(InputImage.fromMediaImage(image, imageInfo.rotationDegrees)).await()
        val landmarkResults = results.allPoseLandmarks
        val detectedLandmarks = mutableListOf<Int>()
        for (landmark in landmarkResults) {
            if (landmark.inFrameLikelihood > 0.7) {
                detectedLandmarks.add(landmark.landmarkType)
            }
        }
        return detectedLandmarks.containsAll(
            listOf(
                PoseLandmark.NOSE, PoseLandmark.LEFT_SHOULDER,
                PoseLandmark.RIGHT_SHOULDER
            )
        )
    }

    @OptIn(ExperimentalGetImage::class)
    private suspend fun runPostDetection() {
        PoseDetection.getClient(
            PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE).build()
        ).use { poseDetector ->
            cameraImageAnalysisUseCase.analyze { imageProxy ->
                imageProxy.image?.let { img ->
                    val poseDetected = poseDetector.detectPersonInFrame(
                        image = img,
                        imageInfo = imageProxy.imageInfo
                    )
                    _state.update { it.copy(detectedPose = poseDetected) }
                }
            }
        }
    }
}

data class CameraState(
    val surfaceRequest: SurfaceRequest? = null,
    val cameraSessionId: Int = 0,
    val imageUri: Uri? = null,
    val detectedPose: Boolean = false,
    val zoomMaxRatio: Float = 1f,
    val zoomMinRatio: Float = 1f,
    val zoomLevel: Float = 1f,
    val canFlipCamera: Boolean = false,
    val isRearCameraActive: Boolean = false,
    val autoFocusUIState: AutoFocusUIState = AutoFocusUIState.Unspecified,
){
    val zoomOptions = when{
        zoomMinRatio <= 0.6f && zoomMaxRatio >= 1f -> listOf(0.6f, 1f)
        zoomMinRatio < 1f && zoomMaxRatio >= 1f -> listOf(zoomMinRatio,1f)
        zoomMinRatio <= 1f && zoomMaxRatio >= 2f -> listOf(1f, 2f)
        zoomMinRatio == zoomMaxRatio -> listOf(zoomMinRatio)
        else -> listOf(zoomMinRatio, zoomMaxRatio)
    }
}

sealed interface AutoFocusUIState {
    data object Unspecified : AutoFocusUIState
    data class Specified(
        val surfaceCoordinates: Offset,
        val status: Status,
    ) : AutoFocusUIState

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILURE,
        CANCELLED
    }
}