package com.muhammad.camera

import androidx.camera.core.AspectRatio
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.muhammad.theme.LimeGreen
import com.muhammad.theme.ProTheme
import com.muhammad.util.dashedRoundedRectBorder
import com.muhammad.util.toPx
import com.muhammad.theme.R as ThemeR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraGuide(
    detectedPose: Boolean,
    modifier: Modifier = Modifier,
    defaultAspectRatio: Float = 9f / 16f,
) {
    Crossfade(
        targetState = detectedPose,
        modifier = modifier,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
    ) { isPose ->
        if (isPose) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .border(
                        2.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(28.dp)
                    )
            ) {
                DecorativeSquiggle(
                    color = LimeGreen,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 20.dp)
                )
                DecorativeSquiggle(
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 27.dp, vertical = 128.dp),
                    alignment = Alignment.BottomStart
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .dashedRoundedRectBorder(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        cornerRadius = 28.dp,
                        intervals = floatArrayOf(10.dp.toPx(), 10.dp.toPx())
                    )
            )
        }
    }
}

@Composable
private fun BoxScope.DecorativeSquiggle(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    alignment: Alignment = Alignment.TopEnd,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAnimation by infiniteTransition.animateFloat(
        0f, 720f, animationSpec = infiniteRepeatable(
            tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Icon(
        painter = rememberVectorPainter(ImageVector.vectorResource(ThemeR.drawable.decorative_squiggle)),
        contentDescription = null,
        tint = color,
        modifier = modifier
            .size(60.dp)
            .align(alignment = alignment)
            .graphicsLayer {
                rotationZ = rotationAnimation
            }
    )
}

@Preview(showBackground = true)
@Composable
private fun CameraGuidePreviewNoDetectedPost() {
    ProTheme {
        CameraGuide(false)
    }
}
@Preview(showBackground = true)
@Composable
private fun CameraGuidePreviewDetectedPost() {
    ProTheme {
        CameraGuide(true)
    }
}