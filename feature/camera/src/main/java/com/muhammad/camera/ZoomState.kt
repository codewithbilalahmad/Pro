package com.muhammad.camera

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.*

@Stable
class ZoomState(
    initialZoomLevel : Float,
    val zoomRange : ClosedFloatingPointRange<Float>,
    val onChangeZoomLevel : (Float) -> Unit
){
    private var functionalZoom = initialZoomLevel
    private val mutatorMutex = MutatorMutex()
    suspend fun absoluteZoom(targetZoomLevel : Float){
        mutatorMutex.mutate {
            functionalZoom = targetZoomLevel.coerceIn(zoomRange)
            onChangeZoomLevel(functionalZoom)
        }
    }
    suspend fun scaleZoom(scalingFactor : Float){
        absoluteZoom(scalingFactor * functionalZoom)
    }
    suspend fun animatedZoom(
        targetZoomLevel : Float,
        animationSpec : AnimationSpec<Float> = tween(durationMillis = 500)
    ){
        mutatorMutex.mutate {
            Animatable(initialValue = functionalZoom).animateTo(
                targetValue = targetZoomLevel, animationSpec = animationSpec
            ){
                functionalZoom = value.coerceIn(zoomRange)
                onChangeZoomLevel(functionalZoom)
            }
        }
    }
}