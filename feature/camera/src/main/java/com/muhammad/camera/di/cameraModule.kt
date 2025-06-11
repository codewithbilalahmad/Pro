package com.muhammad.camera.di

import com.muhammad.camera.CameraViewModel
import com.muhammad.camera.RearCameraUseCase
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val cameraModule = module {
    single { RearCameraUseCase(get()) }
    viewModelOf(::CameraViewModel)
}