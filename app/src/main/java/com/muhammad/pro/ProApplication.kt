package com.muhammad.pro

import android.app.Application
import com.muhammad.camera.di.cameraModule
import com.muhammad.creation.di.creationModule
import com.muhammad.data.di.dataModule
import com.muhammad.home.di.homeModule
import com.muhammad.pro.di.appModule
import com.muhammad.results.di.resultModule
import com.muhammad.util.di.utilModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ProApplication : Application() {
    companion object {
        lateinit var INSTANCE: ProApplication
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        startKoin {
            androidContext(this@ProApplication)
            androidLogger()
            modules(
                appModule, utilModule, dataModule, homeModule, creationModule, cameraModule,
                resultModule
            )
        }
    }
}