package com.muhammad.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.SurfaceRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.layout.FoldingFeature
import com.muhammad.theme.LocalSharedTransitionScope
import com.muhammad.theme.SharedElementKey
import com.muhammad.theme.sharedBoundsRevealWithShapeMorph
import com.muhammad.theme.sharedBoundsWithDefaults
import com.muhammad.util.calculateCorrectAspectRatio
import com.muhammad.util.isTabletTopPosture
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = koinViewModel(),
    onImageCaptured: (Uri) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    var isCameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var shouldCameraPermissionRational by remember {
        mutableStateOf(
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            )
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isCameraPermissionGranted = isGranted
            shouldCameraPermissionRational = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            )
        }
    val sharedTransitionScope = LocalSharedTransitionScope.current
    with(sharedTransitionScope) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .sharedBoundsRevealWithShapeMorph(
                    rememberSharedContentState(SharedElementKey.CameraButtonToFullScreenCamera),
                    targetShape = MaterialShapes.Cookie9Sided,
                    targetValueByState = {
                        when (it) {
                            EnterExitState.PreEnter -> 1f
                            EnterExitState.Visible -> 0f
                            EnterExitState.PostExit -> 1f
                        }
                    }
                )
                .sharedBoundsWithDefaults(rememberSharedContentState(SharedElementKey.CaptureImageToDetails))) {
            if (isCameraPermissionGranted) {
                val state by viewModel.state.collectAsStateWithLifecycle()
                val foldingFeature by viewModel.foldingFeature.collectAsStateWithLifecycle()
                LaunchedEffect(state.imageUri) {
                    val uri = state.imageUri
                    if (uri != null) {
                        onImageCaptured(uri)
                        viewModel.setCaptureImage(null)
                    }
                }
                val scope = rememberCoroutineScope()
                LifecycleStartEffect(viewModel) {
                    val job = scope.launch { viewModel.bindToCamera() }
                    onStopOrDispose { job.cancel() }
                }
                state.surfaceRequest?.let { surface ->
                    CameraPreviewContent(
                        surfaceRequest = surface,
                        modifier = Modifier.fillMaxSize(),
                        tapToFocus = viewModel::tapToFocus,
                        zoomRange = state.zoomMinRatio..state.zoomMaxRatio,
                        zoomLevel = { state.zoomLevel },
                        foldingFeature = foldingFeature,
                        shouldShowRearCameraFeature = viewModel::shouldShowRearDisplayFeature,
                        isRearCameraEnabled = state.isRearCameraActive,
                        autofocusState = state.autoFocusUIState,
                        canFlipCamera = state.canFlipCamera,
                        detectedPose = state.detectedPose,
                        requestFlipCamera = viewModel::flipCameraDirection,
                        requestCaptureImage = viewModel::captureImage,
                        toggleRearCameraFeature = {
                            viewModel.toggleRearDisplayFeature(activity)
                        },
                        cameraSessionId = state.cameraSessionId,
                        defaultZoomOptions = state.zoomOptions,
                        onChangeZoomLevel = viewModel::setZoomLevel
                    )
                }
            } else {
                CameraPermissionGrant(modifier = Modifier.fillMaxSize(), showRationale = shouldCameraPermissionRational, launchPermissionRequest = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                })
            }
        }
    }
}

@Composable
fun CameraPermissionGrant(
    modifier: Modifier = Modifier,
    launchPermissionRequest: () -> Unit,
    showRationale: Boolean,
) {
    LaunchedEffect(showRationale) {
        if (!showRationale) {
            launchPermissionRequest()
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .widthIn(max = 480.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.camera_permission_rationale), textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            launchPermissionRequest()
        }) {
            Text(stringResource(R.string.camera_permission_grant_button))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatelessCameraPreviewContent(
    viewfinder: @Composable (Modifier) -> Unit,
    canFlipCamera: Boolean,
    requestFlipCamera: () -> Unit,
    detectedPose: Boolean,
    defaultZoomOptions: List<Float>,
    zoomLevel: () -> Float,
    onAnimateZoom: (Float) -> Unit,
    requestCaptureImage: () -> Unit,
    modifier: Modifier = Modifier,
    foldingFeature: FoldingFeature? = null,
    shouldShowRearCameraFeature: () -> Boolean = { false },
    toggleRearCameraFeature: () -> Unit = {},
    isRearCameraEnabled: Boolean = false,
) {
    var aspectRatio by remember { mutableFloatStateOf(16f / 9f) }
    val emptyComposable: @Composable (Modifier) -> Unit = {}
    val rearCameraButton: @Composable (Modifier) -> Unit = { rearModifier ->
        RearCameraButton(
            modifier = rearModifier,
            toggleRearCamera = toggleRearCameraFeature, isRearCameraEnabled = isRearCameraEnabled
        )
    }
    CameraLayout(viewfinder = viewfinder, captureButton = { captureModifier ->
        CameraCaptureButton(
            modifier = captureModifier,
            enabled = detectedPose,
            captureImageClicked = requestCaptureImage
        )
    }, flipCameraButton = { flipModifier ->
        if (canFlipCamera) {
            CameraDirectionButton(
                flipCameraDirectionClicked = requestFlipCamera,
                modifier = flipModifier
            )
        }
    }, zoomButton = { zoomModifier ->
        ZoomToolbar(
            defaultZoomOptions = defaultZoomOptions,
            onZoomLevelSelected = onAnimateZoom,
            modifier = zoomModifier, zoomLevel = zoomLevel
        )
    }, guideText = { guideTextModifier ->
        AnimatedVisibility(
            !detectedPose,
            enter = fadeIn(MaterialTheme.motionScheme.slowSpatialSpec()),
            exit = fadeOut(MaterialTheme.motionScheme.slowSpatialSpec()),
            modifier = guideTextModifier
        ) {
            CameraGuideText()
        }
    }, guide = { guideModifier ->
        CameraGuide(
            detectedPose = detectedPose,
            modifier = guideModifier,
            defaultAspectRatio = aspectRatio
        )
    }, isTabletop = isTabletTopPosture(foldingFeature), modifier = modifier.onSizeChanged { size ->
        if (size.height > 0) {
            aspectRatio = calculateCorrectAspectRatio(size.height, size.width, aspectRatio)
        }
    }, rearCameraButton = rearCameraButton)
}

@Composable
fun CameraPreviewContent(
    surfaceRequest: SurfaceRequest,
    autofocusState: AutoFocusUIState,
    tapToFocus: (Offset) -> Unit,
    cameraSessionId: Int,
    canFlipCamera: Boolean,
    requestFlipCamera: () -> Unit,
    detectedPose: Boolean,
    defaultZoomOptions: List<Float>,
    zoomRange: ClosedFloatingPointRange<Float>,
    zoomLevel: () -> Float,
    onChangeZoomLevel: (Float) -> Unit,
    requestCaptureImage: () -> Unit,
    modifier: Modifier = Modifier,
    foldingFeature: FoldingFeature?=null,
    shouldShowRearCameraFeature: () -> Boolean = { false },
    toggleRearCameraFeature: () -> Unit = {},
    isRearCameraEnabled: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val zoomState = remember(cameraSessionId) {
        ZoomState(
            initialZoomLevel = zoomLevel(),
            onChangeZoomLevel = onChangeZoomLevel,
            zoomRange = zoomRange
        )
    }
    StatelessCameraPreviewContent(
        viewfinder = { viewFinderModifier ->
            var aspectRatio by remember { mutableFloatStateOf(9f / 16f) }
            CameraViewFinder(
                surfaceRequest = surfaceRequest,
                autoFocusState = autofocusState,
                tapToFocus = tapToFocus,
                onScaleZoom = { zoom ->
                    scope.launch {
                        zoomState.scaleZoom(zoom)
                    }
                }, modifier = viewFinderModifier.onSizeChanged { size ->
                    if (size.height > 0) {
                        aspectRatio =
                            calculateCorrectAspectRatio(size.height, size.width, aspectRatio)
                    }
                })
        },
        canFlipCamera = canFlipCamera,
        requestFlipCamera = requestFlipCamera,
        detectedPose = detectedPose,
        zoomLevel = zoomLevel, toggleRearCameraFeature = toggleRearCameraFeature,
        shouldShowRearCameraFeature = shouldShowRearCameraFeature,
        modifier = modifier,
        foldingFeature = foldingFeature,
        onAnimateZoom = { zoom ->
            scope.launch {
                zoomState.animatedZoom(zoom)
            }
        },
        defaultZoomOptions = defaultZoomOptions,
        isRearCameraEnabled = isRearCameraEnabled,
        requestCaptureImage = requestCaptureImage
    )
}