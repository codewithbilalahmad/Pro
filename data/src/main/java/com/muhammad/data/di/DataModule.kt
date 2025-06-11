package com.muhammad.data.di

import com.muhammad.data.ConfigProvider
import com.muhammad.data.GeminiNanoDownloader
import com.muhammad.data.GeminiNanoGenerationDataSource
import com.muhammad.data.GeminiNanoGenerationDataSourceImp
import com.muhammad.data.ImageGenerationRepository
import com.muhammad.data.ImageGenerationRepositoryImp
import com.muhammad.data.InternetConnectivityManager
import com.muhammad.data.InternetConnectivityManagerImp
import com.muhammad.data.TextGenerationRepository
import com.muhammad.data.TextGenerationRepositoryImp
import com.muhammad.network.RemoteConfigDataSource
import com.muhammad.network.RemoteConfigDataSourceImp
import com.muhammad.network.vertexai.FirebaseAIDataSource
import com.muhammad.network.vertexai.FirebaseAIDataSourceImp
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { FirebaseAIDataSourceImp(get()) }.bind<FirebaseAIDataSource>()
    single { RemoteConfigDataSourceImp() }.bind<RemoteConfigDataSource>()
    single { ConfigProvider(get()) }
    single { GeminiNanoDownloader(get()) }
    single { GeminiNanoGenerationDataSourceImp(get()) }.bind<GeminiNanoGenerationDataSource>()
    single { InternetConnectivityManagerImp(get()) }.bind<InternetConnectivityManager>()
    single {
        ImageGenerationRepositoryImp(
            localFileProvider = get(),
            internetConnectivityManager = get(),
            geminiNanoGenerationDataSource = get(),
            firebaseAIDataSource = get()
        )
    }.bind<ImageGenerationRepository>()
    single {
        TextGenerationRepositoryImp(get(),get(),get())
    }.bind<TextGenerationRepository>()
}