package com.muhammad.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.muhammad.theme.TertiaryContainer
import com.muhammad.util.allowFullContent
import com.muhammad.util.isAtLeastMedium
import com.muhammad.util.shouldShowTabletopLayout
import com.muhammad.util.supportTabletTop

@Composable
fun CameraLayout(
    modifier: Modifier = Modifier,
    viewfinder: @Composable (modifier: Modifier) -> Unit,
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: @Composable (modifier: Modifier) -> Unit,
    zoomButton: @Composable (modifier: Modifier) -> Unit,
    guideText: @Composable (modifier: Modifier) -> Unit,
    guide: @Composable (modifier: Modifier) -> Unit,
    rearCameraButton: @Composable (modifier: Modifier) -> Unit,
    supportsTabletop: Boolean = supportTabletTop(),
    isTabletop: Boolean = false,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(TertiaryContainer)
    ) {
        when {
            isAtLeastMedium() && shouldShowTabletopLayout(
                supportsTabletop = supportsTabletop,
                isTabletop = isTabletop
            ) -> TableTopSupportedCameraLayout(
                viewfinder = viewfinder,
                captureButton = captureButton,
                flipCameraButton = flipCameraButton,
                guideText = guideText, zoomButton = zoomButton,
                guide = guide,
                rearCameraButton = rearCameraButton,
                isTableTop = isTabletop
            )

            isAtLeastMedium() && maxWidth > maxHeight -> MediumHorizontalCameraLayout(
                viewfinder = viewfinder,
                captureButton = captureButton, zoomButton = zoomButton,
                flipCameraButton = flipCameraButton,
                guide = guide,
                guideText = guideText,
            )

            this.maxWidth > maxHeight && allowFullContent() -> CompactHorizontalCameraLayout(
                viewfinder = viewfinder,
                captureButton = captureButton,
                guide = guide, flipCameraButton = flipCameraButton,
                guideText = guideText,
                zoomButton = zoomButton
            )

            this.maxWidth > maxHeight && !allowFullContent() -> SubCompactHorizontalCameraLayout(
                viewfinder = viewfinder,
                captureButton = captureButton,
                guide = guide,
                guideText = guideText,
                flipCameraButton = flipCameraButton
            )

            else -> VerticalCameraLayout(
                viewFinder = viewfinder,
                captureButton = captureButton,
                flipCameraButton = flipCameraButton,
                zoomButton = zoomButton,
                guide = guide,
                guideText = guideText
            )
        }
    }
}

@Composable
fun CompactHorizontalCameraLayout(
    viewfinder: @Composable (modifier: Modifier) -> Unit,
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: @Composable (modifier: Modifier) -> Unit,
    zoomButton: @Composable (modifier: Modifier) -> Unit,
    guideText: @Composable (modifier: Modifier) -> Unit,
    guide: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxSize()) {
        VerticalControlsLayout(
            captureButton = captureButton,
            flipCameraButton = flipCameraButton,
            zoomButton = zoomButton,
            modifier = Modifier.weight(1f)
        )
        Box(Modifier.weight(1f)) {
            viewfinder(Modifier)
            guide(Modifier.fillMaxSize())
        }
        Box(
            Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            guideText(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun SubCompactHorizontalCameraLayout(
    viewfinder: @Composable (modifier: Modifier) -> Unit,
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: @Composable (modifier: Modifier) -> Unit,
    guideText: @Composable (modifier: Modifier) -> Unit,
    guide: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxSize()) {
        VerticalControlsLayout(
            captureButton = captureButton,
            flipCameraButton = flipCameraButton,
            null,
            modifier = Modifier.weight(1f)
        )
        Box(modifier = Modifier.weight(1f)) {
            viewfinder(Modifier)
            guide(Modifier.fillMaxSize())
            guideText(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
fun TableTopSupportedCameraLayout(
    viewfinder: @Composable (modifier: Modifier) -> Unit,
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: @Composable (modifier: Modifier) -> Unit,
    zoomButton: @Composable (modifier: Modifier) -> Unit,
    guideText: @Composable (modifier: Modifier) -> Unit,
    guide: @Composable (modifier: Modifier) -> Unit,
    rearCameraButton: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier,
    isTableTop: Boolean = false,
) {
    if (isTableTop) {
        TableTopCameraLayout(
            viewfinder = viewfinder,
            captureButton = captureButton,
            flipCameraButton = flipCameraButton,
            zoomButton = zoomButton,
            guide = guide,
            guideText = guideText, rearCameraButton = rearCameraButton, modifier = modifier
        )
    } else {
        TableTopReadyCameraLayout(
            viewfinder = viewfinder,
            captureButton = captureButton,
            flipCameraButton = flipCameraButton,
            zoomButton = zoomButton,
            guide = guide,
            guideText = guideText, rearCameraButton = rearCameraButton, modifier = modifier
        )
    }
}

@Composable
private fun TableTopReadyCameraLayout(
    viewfinder: @Composable (modifier: Modifier) -> Unit,
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: @Composable (modifier: Modifier) -> Unit,
    zoomButton: @Composable (modifier: Modifier) -> Unit,
    guideText: @Composable (modifier: Modifier) -> Unit,
    guide: @Composable (modifier: Modifier) -> Unit,
    rearCameraButton: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom) {
        Box(
            modifier = Modifier
                .weight(1f)
                .safeDrawingPadding()
        ) {
            viewfinder(Modifier.fillMaxSize())
            guide(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            )
            guideText(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 36.dp, vertical = 64.dp)
            )
        }
        HorizontalControlsLayout(
            captureButton = captureButton,
            flipCameraButton = flipCameraButton,
            zoomButton = zoomButton,
            rearCameraButton = rearCameraButton,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun TableTopCameraLayout(
    viewfinder: @Composable (modifier: Modifier) -> Unit,
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: @Composable (modifier: Modifier) -> Unit,
    zoomButton: @Composable (modifier: Modifier) -> Unit,
    guideText: @Composable (modifier: Modifier) -> Unit,
    guide: @Composable (modifier: Modifier) -> Unit,
    rearCameraButton: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        ) {
            viewfinder(Modifier)
            guide(Modifier.fillMaxSize())
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(top = 48.dp)
        ) {
            guideText(Modifier.align(Alignment.TopCenter))
            HorizontalControlsLayout(
                captureButton = captureButton,
                flipCameraButton = flipCameraButton,
                zoomButton = zoomButton,
                rearCameraButton = rearCameraButton,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun MediumHorizontalCameraLayout(
    modifier: Modifier = Modifier,
    viewfinder: @Composable (modifier: Modifier) -> Unit,
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: @Composable (modifier: Modifier) -> Unit,
    zoomButton: @Composable (modifier: Modifier) -> Unit,
    guideText: @Composable (modifier: Modifier) -> Unit,
    guide: @Composable (modifier: Modifier) -> Unit,
) {
    Row(modifier.fillMaxSize()) {
        VerticalControlsLayout(
            captureButton = captureButton,
            flipCameraButton = flipCameraButton,
            zoomButton = null,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .aspectRatio(3 / 4f)
                .navigationBarsPadding()
        ) {
            viewfinder(Modifier.fillMaxSize())
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.weight(1f)) {
                    guide(Modifier.fillMaxSize())
                    guideText(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
                zoomButton(Modifier)
            }
        }
        Spacer(
            Modifier
                .fillMaxHeight()
                .weight(1f)
        )
    }
}

@Composable
fun VerticalCameraLayout(
    modifier: Modifier = Modifier,
    viewFinder: @Composable (modifier: Modifier) -> Unit,
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: (@Composable (modifier: Modifier) -> Unit)?,
    zoomButton: (@Composable (modifier: Modifier) -> Unit)?,
    rearCameraButton: (@Composable (modifier: Modifier) -> Unit)? = null,
    guideText: @Composable (modifier: Modifier) -> Unit,
    guide: @Composable (modifier: Modifier) -> Unit,
) {
    Box(modifier = modifier) {
        viewFinder(Modifier)
        Column(modifier.safeContentPadding()) {
            Box(Modifier.weight(1f)) {
                guide(Modifier.fillMaxSize())
                guideText(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 12.dp)
                )
            }
            HorizontalControlsLayout(
                captureButton = captureButton,
                flipCameraButton = flipCameraButton,
                rearCameraButton = rearCameraButton,
                zoomButton = zoomButton
            )
        }
    }
}

@Composable
private fun HorizontalControlsLayout(
    modifier: Modifier = Modifier,
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: (@Composable (modifier: Modifier) -> Unit)?,
    zoomButton: (@Composable (modifier: Modifier) -> Unit)?,
    rearCameraButton: (@Composable (modifier: Modifier) -> Unit)? = null,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (zoomButton != null) {
            zoomButton(Modifier)
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (flipCameraButton != null) {
                    flipCameraButton(Modifier)
                }
            }
            captureButton(Modifier)
            if (rearCameraButton != null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    rearCameraButton(Modifier)
                }
            } else {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun VerticalControlsLayout(
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: (@Composable (modifier: Modifier) -> Unit)?,
    zoomButton: (@Composable (modifier: Modifier) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (flipCameraButton != null) {
                    flipCameraButton(Modifier)
                }
            }
            captureButton(Modifier)
            Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.width(12.dp))
        if (zoomButton != null) {
            zoomButton(Modifier)
        }
    }
}