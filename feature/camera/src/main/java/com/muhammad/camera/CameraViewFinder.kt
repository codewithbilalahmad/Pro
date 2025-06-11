package com.muhammad.camera

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.muhammad.theme.Error
import com.muhammad.theme.LimeGreen
import com.muhammad.theme.Surface

private val TAP_TO_FOCUS_INDICATOR_SIZE = 48.dp

@Composable
fun CameraViewFinder(
    surfaceRequest: SurfaceRequest,
    autoFocusState: AutoFocusUIState,
    tapToFocus: (Offset) -> Unit,
    onScaleZoom: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onScaleCurrentZoom by rememberUpdatedState(onScaleZoom)
    val currentTapToFocus by rememberUpdatedState(tapToFocus)
    val coordinateTransform = remember { MutableCoordinateTransformer() }
    CameraXViewfinder(
        surfaceRequest = surfaceRequest,
        coordinateTransformer = coordinateTransform,
        modifier = modifier
            .pointerInput(coordinateTransform) {
                detectTapGestures { tapCoords ->
                    with(coordinateTransform) {
                        currentTapToFocus(tapCoords.transform())
                    }
                }
            }
            .transformable(rememberTransformableState(onTransformation = { zoomChange, _, _ ->
                onScaleCurrentZoom(zoomChange)
            }))
    )
    if (autoFocusState is AutoFocusUIState.Specified) {
        val showAutofocusIndicator = autoFocusState.status == AutoFocusUIState.Status.RUNNING
        val tapCoords =
            remember(coordinateTransform.transformMatrix, autoFocusState.surfaceCoordinates) {
                Matrix().run {
                    setFrom(coordinateTransform.transformMatrix)
                    invert()
                    map(autoFocusState.surfaceCoordinates)
                }
            }
        AnimatedVisibility(
            visible = showAutofocusIndicator,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .offset {
                    tapCoords.round()
                }
                .offset(
                    x = -TAP_TO_FOCUS_INDICATOR_SIZE / 2,
                    y = -TAP_TO_FOCUS_INDICATOR_SIZE / 2
                )
        ) {
            Spacer(
                modifier = Modifier
                    .border(
                        2.dp, when (autoFocusState.status) {
                            AutoFocusUIState.Status.SUCCESS -> LimeGreen
                            AutoFocusUIState.Status.FAILURE -> Error
                            else -> Surface
                        }, CircleShape
                    )
                    .size(TAP_TO_FOCUS_INDICATOR_SIZE)
            )
        }
    }
}