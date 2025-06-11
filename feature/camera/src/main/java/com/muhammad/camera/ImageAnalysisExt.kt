package com.muhammad.camera

import androidx.camera.core.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun ImageAnalysis.analyze(block : suspend (ImageProxy) -> Unit){
    coroutineScope {
        try {
            suspendCancellableCoroutine<Unit>{cont ->
                setAnalyzer(Runnable::run){imageProxy ->
                    launch(start = CoroutineStart.ATOMIC){
                        imageProxy.use { if(cont.isActive) block(it) }
                    }
                }
            }
        } finally {
            clearAnalyzer()
        }
    }
}