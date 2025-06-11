package com.muhammad.camera

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import com.muhammad.theme.Primary80

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RearCameraButton(
    modifier: Modifier = Modifier,
    isRearCameraEnabled: Boolean = false,
    toggleRearCamera: () -> Unit,
) {
    val actionLabel = stringResource(R.string.rear_camera_description)
    val colors = if (isRearCameraEnabled) {
        IconButtonDefaults.filledTonalIconButtonColors().copy(
            containerColor = Primary80,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        IconButtonDefaults.filledTonalIconButtonColors()
    }
    FilledTonalIconButton(
        onClick = toggleRearCamera,
        modifier = modifier
            .semantics {
                onClick(label = actionLabel, action = {
                    toggleRearCamera()
                    true
                })
            }
            .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Narrow)),
        shape = IconButtonDefaults.filledShape,
        colors = colors) {
        Icon(
            painter = painterResource(id = R.drawable.outline_rear_camera),
            contentDescription = null
        )
    }
}