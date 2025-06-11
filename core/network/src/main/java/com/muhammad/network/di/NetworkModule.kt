package com.muhammad.network.di

import android.os.Build.VERSION.SDK_INT
import coil3.ImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.muhammad.network.RemoteConfigDataSource
import com.muhammad.network.RemoteConfigDataSourceImp
import com.muhammad.network.vertexai.FirebaseAIDataSource
import com.muhammad.network.vertexai.FirebaseAIDataSourceImp
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val networkModule = module {
    single{
        Json{
            ignoreUnknownKeys = true
        }
    }
    single {
        OkHttpClient.Builder()
            .connectTimeout(120L, TimeUnit.SECONDS)
            .readTimeout(120L, TimeUnit.SECONDS)
            .writeTimeout(120L, TimeUnit.SECONDS)
            .build()
    }

    single {
        ImageLoader.Builder(get()).components {
            if(SDK_INT >= 28){
                add(AnimatedImageDecoder.Factory())
            } else{
                add(GifDecoder.Factory())
            }
            add(OkHttpNetworkFetcherFactory(callFactory = {
                get()
            }))
        }.memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED).crossfade(true).build()
    }
    single { RemoteConfigDataSourceImp() }.bind<RemoteConfigDataSource>()
    single { FirebaseAIDataSourceImp(get()) }.bind<FirebaseAIDataSource>()

}