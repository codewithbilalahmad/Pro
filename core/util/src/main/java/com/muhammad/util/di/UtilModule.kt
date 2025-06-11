package com.muhammad.util.di

import com.muhammad.util.LocalFileProvider
import com.muhammad.util.LocalFileProviderImp
import org.koin.dsl.bind
import org.koin.dsl.module

val utilModule = module {
    single {  }
    single { LocalFileProviderImp(get()) }.bind<LocalFileProvider>()
}