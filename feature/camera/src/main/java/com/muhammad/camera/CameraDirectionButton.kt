package com.muhammad.camera

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.muhammad.theme.ProTheme

@Preview
@Composable
private fun CameraDirectionButtonPreview() {
    ProTheme {
        CameraDirectionButton { }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraDirectionButton(modifier: Modifier = Modifier, flipCameraDirectionClicked: () -> Unit) {
    val actionLabel = stringResource(R.string.flip_camera_direction)
    FilledTonalIconButton(
        onClick = flipCameraDirectionClicked, modifier = modifier
            .semantics {
                onClick(label = actionLabel, action = {
                    flipCameraDirectionClicked()
                    true
                })
            }
            .size(
                IconButtonDefaults.mediumContainerSize(
                    IconButtonDefaults.IconButtonWidthOption.Narrow
                )
            ), shape = IconButtonDefaults.filledShape
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_camera_switch),
            contentDescription = null,
        )
    }
}