package com.muhammad.pro.navigation

import kotlinx.serialization.Serializable

sealed interface Destinations{
    @Serializable
    data object Home : Destinations
    @Serializable
    data class Create(val fileName : String?=null, val prompt : String?=null) : Destinations
    @Serializable
    data object Camera : Destinations
    @Serializable
    data object About : Destinations
}