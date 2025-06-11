package com.muhammad.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.muhammad.theme.ProTheme

@Preview
@Composable
private fun CameraControlsPreview() {
    ProTheme {
        CameraControls(
            captureImageClicked = {},
            flipCameraDirectionClicked = {},
            defaultZoomOptions = listOf(0.6f, 1f),
            detectedPose = true,
            zoomLevel = { 0.4f },
            canFlipCamera = true,
            onZoomLevelSelected = {})
    }
}

@Composable
fun CameraControls(
    modifier: Modifier = Modifier,
    captureImageClicked: () -> Unit,
    canFlipCamera: Boolean,
    flipCameraDirectionClicked: () -> Unit,
    detectedPose: Boolean = false,
    defaultZoomOptions: List<Float>,
    zoomLevel: () -> Float,
    onZoomLevelSelected: (Float) -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        ZoomToolbar(
            defaultZoomOptions = defaultZoomOptions,
            zoomLevel = zoomLevel,
            onZoomLevelSelected = onZoomLevelSelected
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (canFlipCamera) {
                    CameraDirectionButton(flipCameraDirectionClicked = flipCameraDirectionClicked)
                }
            }
            CameraCaptureButton(enabled = detectedPose) {
                captureImageClicked()
            }
            Spacer(Modifier.weight(1f))
        }
    }
}