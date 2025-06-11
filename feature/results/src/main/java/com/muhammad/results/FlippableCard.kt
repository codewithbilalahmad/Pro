package com.muhammad.results

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.muhammad.theme.ProTheme

enum class FlippableState {
    Front, Back;

    fun toggle(): FlippableState {
        return when (this) {
            Front -> Back
            Back -> Front
        }
    }
}

@Preview
@Composable
private fun FlippableCardPreview() {
    ProTheme {
        val resultImage = ImageBitmap.imageResource(R.drawable.placeholderbot)
        FlippableCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp), back = {
        }, front = {
            FrontCard(bitmap = resultImage.asAndroidBitmap())
        }, flippableState = FlippableState.Front
        )
    }
}

@Composable
fun FlippableCard(
    modifier: Modifier = Modifier,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit,
    flipDurationMillis: Int = 1000,
    flippableState: FlippableState = FlippableState.Front,
    onFlipStateChanged: ((FlippableState) -> Unit)? = null,
) {
    val transition = updateTransition(flippableState)
    val frontRotation by getFrontRotation(transition = transition, flipMs = flipDurationMillis)
    val backRotation by getBackRotation(transition = transition, flipMs = flipDurationMillis)
    val opacityFront by getFrontOpacitySpec(transition = transition, flipMs = flipDurationMillis)
    val opacityBack by getBackOpacitySpec(transition = transition, flipMs = flipDurationMillis)
    val cameraDistance = 30f
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onFlipStateChanged?.invoke(flippableState.toggle())
        }) {
        Box(Modifier.graphicsLayer {
            this.cameraDistance = cameraDistance
            rotationY = backRotation
            alpha = opacityBack
        }) {
            back()
        }
        Box(
            Modifier
                .graphicsLayer {
                    this.cameraDistance = cameraDistance
                    rotationY = frontRotation
                    alpha = opacityFront
                }
                .zIndex(1F - opacityFront)) {
            front()
        }
    }
}

@Composable
private fun getFrontOpacitySpec(
    transition: Transition<FlippableState>,
    flipMs: Int,
) = transition.animateFloat(transitionSpec = {
    when {
        FlippableState.Front isTransitioningTo FlippableState.Back -> keyframes {
            durationMillis = flipMs
            1f at 0
            1f at (flipMs / 2) - 1
            0f at flipMs / 2
            0f at flipMs
        }

        FlippableState.Back isTransitioningTo FlippableState.Front -> keyframes {
            durationMillis = flipMs
            0f at 0
            0f at (flipMs / 2) - 1
            1f at flipMs / 2
            1f at flipMs
        }

        else -> snap()
    }
}, label = "animate front") { state ->
    when (state) {
        FlippableState.Front -> 1f
        FlippableState.Back -> 0f
    }
}

@Composable
private fun getBackOpacitySpec(
    transition: Transition<FlippableState>,
    flipMs: Int,
) = transition.animateFloat(
    transitionSpec = {
        when {
            FlippableState.Front isTransitioningTo FlippableState.Back -> {
                keyframes {
                    durationMillis = flipMs
                    0f at 0
                    1f at (flipMs / 2) - 1
                    1f at flipMs / 2
                    1f at flipMs
                }
            }

            FlippableState.Back isTransitioningTo FlippableState.Front -> {
                keyframes {
                    durationMillis = flipMs
                    1f at 0
                    1f at (flipMs / 2) - 1
                    0f at (flipMs / 2)
                    0f at flipMs
                }
            }

            else -> snap()
        }
    }, label = "Back Opacity"
) { state ->
    when (state) {
        FlippableState.Front -> 0f
        FlippableState.Back -> 1f
    }
}

@Composable
private fun getBackRotation(
    transition: Transition<FlippableState>,
    flipMs: Int,
) = transition.animateFloat(transitionSpec = {
    when {
        FlippableState.Front isTransitioningTo FlippableState.Back -> {
            keyframes {
                durationMillis = flipMs
                -90f at 0
                -90f at flipMs / 2
                0f at flipMs
            }
        }

        FlippableState.Back isTransitioningTo FlippableState.Front -> {
            keyframes {
                durationMillis = flipMs
                0f at 0
                -90f at flipMs / 2
                -90f at flipMs
            }
        }

        else -> snap()
    }
}, label = "Back Rotation") { state ->
    when (state) {
        FlippableState.Front -> 180f
        FlippableState.Back -> 0f
    }
}

@Composable
private fun getFrontRotation(
    transition: Transition<FlippableState>,
    flipMs: Int,
) = transition.animateFloat(
    transitionSpec = {
        when {
            FlippableState.Front isTransitioningTo FlippableState.Back -> {
                keyframes {
                    durationMillis = flipMs
                    0f at 0
                    90f at flipMs / 2
                    90f at flipMs
                }
            }

            FlippableState.Back isTransitioningTo FlippableState.Front -> {
                keyframes {
                    durationMillis = flipMs
                    90f at 0
                    90f at flipMs / 2
                    0f at flipMs
                }
            }

            else -> snap()
        }
    }, label = "front Rotation"
) { state ->
    when (state) {
        FlippableState.Front -> 0f
        FlippableState.Back -> 180f
    }
}