package com.muhammad.data

import com.muhammad.network.RemoteConfigDataSource

class ConfigProvider(
    private val remoteConfigDataSource: RemoteConfigDataSource
){
    fun isAppInActive() : Boolean{
        return remoteConfigDataSource.isAppInActive()
    }
    fun getPromoVideoLink() : String{
        return remoteConfigDataSource.getPromoVideoLink()
    }
    fun getDancingDroidLink() : String{
        return remoteConfigDataSource.getDancingDroidLink()
    }
}