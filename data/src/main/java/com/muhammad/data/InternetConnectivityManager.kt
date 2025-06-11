package com.muhammad.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

interface InternetConnectivityManager{
    fun isInternetAvailable() : Boolean
}
class InternetConnectivityManagerImp(
    private val context : Context
) : InternetConnectivityManager{
    override fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?:  return  false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}