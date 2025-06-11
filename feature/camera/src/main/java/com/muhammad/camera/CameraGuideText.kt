package com.muhammad.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraGuideText(modifier: Modifier = Modifier) {
    BasicText(
        text = stringResource(R.string.camera_guide_text_label),
        style = MaterialTheme.typography.bodyMediumEmphasized,
        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.bodyMediumEmphasized.fontSize),
        maxLines = 1,
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceContainerHighest,
                RoundedCornerShape(24.dp)
            )
            .padding(10.dp)
    )
}