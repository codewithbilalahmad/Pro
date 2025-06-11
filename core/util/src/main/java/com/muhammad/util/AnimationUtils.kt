package com.muhammad.util

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.approachLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.round
import kotlinx.coroutines.delay
import java.text.BreakIterator
import java.text.StringCharacterIterator


@Composable
fun AnimatedTextField(
    textFieldState: TextFieldState,
    targetEndState: String? = null,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    decorator: TextFieldDecorator? = null,
) {
    val breakIterator = remember(targetEndState) { BreakIterator.getCharacterInstance() }
    val typingDelayInMs = 30L
    LaunchedEffect(targetEndState) {
        if (targetEndState != null && !textFieldState.text.startsWith(targetEndState)) {
            delay(200)
            breakIterator.text = StringCharacterIterator(targetEndState)
            var nextIndex = breakIterator.next()
            while (nextIndex != BreakIterator.DONE) {
                textFieldState.edit {
                    replace(0, length, targetEndState.substring(0, nextIndex).toString())
                }
                nextIndex = breakIterator.next()
                delay(typingDelayInMs)
            }
        }
    }
    BasicTextField(
        state = textFieldState,
        modifier = modifier,
        textStyle = style,
        decorator = decorator
    )
}

/**
 * Skips to the end size for a particular composable, skipping through the intermediate animated sizes.
 * Similar to skipToLookaheadSize, but for placement instead.
 * This is useful if you'd like your content to be placed in its final position and not have any animations affect its layout.
 * See the usage on the CameraPreviewScreen composable, we want the camera contents to remain in place,
 * but the animation should perform a progressive reveal.
 *
 * @param scope The SharedTransitionScope where the transition is taking place.
 * @return Modifier chain.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.skipToLookaheadPlacement(scope: SharedTransitionScope): Modifier =
    this.approachLayout(
        isMeasurementApproachInProgress = { false },
        isPlacementApproachInProgress = { scope.isTransitionActive }) { measurables, constraints ->
        measurables.measure(constraints).run {
            layout(width = width, height = height) {
                coordinates?.let { coord ->
                    with(scope) {
                        val target = lookaheadScopeCoordinates.localLookaheadPositionOf(coord)
                        val actual = lookaheadScopeCoordinates.localPositionOf(coord)
                        place((target - actual).round())
                    }
                } ?: place(0, 0)
            }
        }
    }