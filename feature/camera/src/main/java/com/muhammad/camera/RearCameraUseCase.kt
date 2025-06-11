package com.muhammad.camera

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.area.WindowAreaCapability
import androidx.window.area.WindowAreaController
import androidx.window.area.WindowAreaInfo
import androidx.window.area.WindowAreaSession
import androidx.window.area.WindowAreaSessionCallback
import androidx.window.core.ExperimentalWindowApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalWindowApi::class)
class RearCameraUseCase(
    private val context: Context,
) : WindowAreaSessionCallback {
    private val windowAreaController = WindowAreaController.getOrCreate()
    private val displayExecutor = ContextCompat.getMainExecutor(context)
    private var windowAreaInfo: WindowAreaInfo? = null
    private var windowAreaSession: WindowAreaSession? = null
    private var capabilityStatus: WindowAreaCapability.Status =
        WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED
    private val rearDisplayOperation =
        WindowAreaCapability.Operation.OPERATION_TRANSFER_ACTIVITY_TO_AREA
    private var isSessionAction = false
    fun init(activity: ComponentActivity) {
        activity.lifecycleScope.launch(Dispatchers.Main) {
            activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowAreaController.windowAreaInfos.map { info ->
                    info.firstOrNull {
                        it.type == WindowAreaInfo.Type.TYPE_REAR_FACING
                    }
                }.onEach { info ->
                    windowAreaInfo = info
                }.map {
                    it?.getCapability(rearDisplayOperation)?.status
                        ?: WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED
                }.distinctUntilChanged().collect {
                    capabilityStatus = it
                }
            }
        }
    }

    fun shouldDisplayRearCameraButton(): Boolean {
        return capabilityStatus == WindowAreaCapability.Status.WINDOW_AREA_STATUS_AVAILABLE ||
                capabilityStatus == WindowAreaCapability.Status.WINDOW_AREA_STATUS_ACTIVE
    }

    fun isRearCameraActive(): Boolean = isSessionAction
    fun toggleRearCameraDisplay(activity: ComponentActivity) {
        if (capabilityStatus == WindowAreaCapability.Status.WINDOW_AREA_STATUS_ACTIVE) {
            if (windowAreaSession == null) {
                windowAreaSession = windowAreaInfo?.getActiveSession(
                    rearDisplayOperation
                )
            }
            windowAreaSession?.close()
            isSessionAction = false
        } else {
            windowAreaInfo?.token?.let { token ->
                windowAreaController.transferActivityToWindowArea(
                    activity = activity,
                    token = token,
                    executor = displayExecutor,
                    windowAreaSessionCallback = this,
                )
                isSessionAction = true
            }
        }
    }

    override fun onSessionEnded(t: Throwable?) = Unit

    override fun onSessionStarted(session: WindowAreaSession) {
        windowAreaSession = session
    }
}