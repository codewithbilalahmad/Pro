package com.muhammad.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.muhammad.theme.ProTheme
import com.muhammad.theme.R
import com.muhammad.util.LargeScreensPreview
import com.muhammad.util.PhonePreview
import com.muhammad.util.backgroundRepeatX
import com.muhammad.util.isAtLeastMedium
import com.muhammad.util.toPx

@Composable
fun SquiggleBackground(
    modifier: Modifier = Modifier,
    offsetHeightFraction: Float = 0f,
    isMediumWindowSize: Boolean = isAtLeastMedium(),
) {
    val vectorBackground =
        rememberVectorPainter(ImageVector.vectorResource(R.drawable.squiggle))
    val verticalPadding = if (!isMediumWindowSize) 64.dp else 0.dp

    BoxWithConstraints(
        modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
    ) {
        val maxHeight = this@BoxWithConstraints.maxHeight.toPx()
        Image(
            painter = vectorBackground,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = if (isMediumWindowSize) 1.3f else 1f
                    scaleY = if (isMediumWindowSize) 1.3f else 1f
                }
                .offset {
                    IntOffset(0, y = (maxHeight * offsetHeightFraction).toInt())
                }
                .padding(top = verticalPadding, bottom = verticalPadding),
            contentScale = ContentScale.FillHeight,
        )
    }
}

@Composable
fun ScallopBackground(modifier: Modifier = Modifier) {
    val vectorBackground =
        rememberVectorPainter(ImageVector.vectorResource(R.drawable.shape_home_bg))
    val backgroundWidth = 300.dp
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
    ) {
        val maxHeight = this@BoxWithConstraints.maxHeight.toPx()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(0, y = (maxHeight * 0.6f).toInt())
                }
                .backgroundRepeatX(vectorBackground, backgroundWidth.toPx()),
        )
    }
}

@LargeScreensPreview
@Composable
private fun SquiggleBackgroundLargePreview() {
    ProTheme {
        SquiggleBackground(modifier = Modifier.fillMaxSize(), offsetHeightFraction = 0.5f)
    }
}

@PhonePreview
@Composable
private fun SquiggleBackgroundPreview() {
    ProTheme {
        SquiggleBackground(modifier = Modifier.fillMaxSize(), offsetHeightFraction = 0.5f)
    }
}

@LargeScreensPreview
@Composable
private fun ResultsBackgroundLargePreview() {
    ProTheme {
        ResultsBackground(isMediumWindowSize = true, modifier = Modifier.fillMaxSize())
    }
}

@PhonePreview
@Composable
private fun ResultsBackgroundPhonePreview() {
    ProTheme {
        ResultsBackground(isMediumWindowSize = false, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun ResultsBackground(
    modifier: Modifier = Modifier,
    isMediumWindowSize: Boolean = isAtLeastMedium(),
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.primary)
            .verticalMarquee(
                iterations = Int.MAX_VALUE,
                repeatDelayMillis = 0,
                spacing = MarqueeSpacing(0.dp),
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            repeat(10) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(if (isMediumWindowSize) 300.dp else 175.dp)
                        .graphicsLayer {
                            scaleX = 1.01f
                        }
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(50),
                        ),
                )
            }
        }
    }
}
