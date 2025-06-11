package com.muhammad.creation.di

import com.muhammad.creation.CreationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val creationModule = module {
    viewModelOf(::CreationViewModel)
}