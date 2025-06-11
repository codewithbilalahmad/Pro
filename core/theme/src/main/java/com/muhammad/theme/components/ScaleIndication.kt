package com.muhammad.theme.components


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScaleIndicationNode(
    private val interactionSource: InteractionSource,
    private val animationSpec: AnimationSpec<Float>,
    private val targetScale: Float = 1.04f,
) : Modifier.Node(), DrawModifierNode {
    val animatedScalePercent = Animatable(1f)

    private suspend fun animateToPressed() {
        animatedScalePercent.animateTo(targetScale, animationSpec)
    }

    private suspend fun animateToResting() {
        animatedScalePercent.animateTo(1f, animationSpec)
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> animateToPressed()
                    is PressInteraction.Release -> animateToResting()
                    is PressInteraction.Cancel -> animateToResting()
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        scale(animatedScalePercent.value) { this@draw.drawContent() }
    }
}

@Stable
class ScaleIndicationNodeFactory(
    private val animationSpec: AnimationSpec<Float>,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return ScaleIndicationNode(interactionSource, animationSpec)
    }

    override fun hashCode(): Int = -1

    override fun equals(other: Any?) = other === this
}
