package com.muhammad.results.di

import com.muhammad.results.ResultViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val resultModule = module {
    viewModelOf(::ResultViewModel)
}