package com.muhammad.camera

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

suspend fun <R> ProcessCameraProvider.runWith(
    cameraSelector: CameraSelector,
    useCase : UseCaseGroup,
    block : suspend CoroutineScope.(Camera) -> R
) : R = coroutineScope {
    val scopedLifeCycle = CoroutineLifecycleOwner(coroutineContext)
    block(this@runWith.bindToLifecycle(scopedLifeCycle, cameraSelector, useCase))
}
internal class CoroutineLifecycleOwner(coroutineContext : CoroutineContext) : LifecycleOwner{
    private val lifecycleRegistry = LifecycleRegistry(this).apply {
        currentState = Lifecycle.State.INITIALIZED
    }
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
    init {
        if(coroutineContext[Job]?.isActive == true){
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
            coroutineContext[Job]?.invokeOnCompletion {
                lifecycleRegistry.apply {
                    currentState = Lifecycle.State.STARTED
                    currentState = Lifecycle.State.CREATED
                    currentState = Lifecycle.State.DESTROYED
                }
            }
        } else{
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }
    }
}