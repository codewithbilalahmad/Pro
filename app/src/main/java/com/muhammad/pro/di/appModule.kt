package com.muhammad.pro.di

import com.muhammad.pro.ProApplication
import org.koin.dsl.module

val appModule = module {
    single { ProApplication }
    single { ProApplication.INSTANCE }
}