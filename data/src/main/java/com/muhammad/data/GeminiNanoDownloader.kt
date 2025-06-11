package com.muhammad.data

import android.content.Context
import com.google.ai.edge.aicore.DownloadCallback
import com.google.ai.edge.aicore.DownloadConfig
import com.google.ai.edge.aicore.GenerativeAIException
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.generationConfig

class GeminiNanoDownloader(
    private val context: Context,
) {
    var generativeModel : GenerativeModel?=null
        private set
    private var modelDownloaded  = false
    fun isModalDownloaded() = modelDownloaded
    suspend fun downloadModel(){
        try {
            setUp()
            generativeModel?.prepareInferenceEngine()
        } catch (e : Exception){
            println("Error downloading model : ${e.message}")
        }
    }
    private fun setUp(){
        val downloadCallBack = object  : DownloadCallback{
            override fun onDownloadStarted(bytesToDownload: Long) {
                super.onDownloadStarted(bytesToDownload)
                println("Gemini Nano Modal is started downloading...")
            }

            override fun onDownloadCompleted() {
                super.onDownloadCompleted()
                modelDownloaded = true
                println("Gemini Nano Modal download Completed")
            }

            override fun onDownloadFailed(failureStatus: String, e: GenerativeAIException) {
                super.onDownloadFailed(failureStatus, e)
                generativeModel = null
                println("Gemini Nano Modal download Failed")
            }
        }
        val downloadConfig = DownloadConfig()
        val generationConfig = generationConfig{
            context = context
            temperature = 0.2f
            topK = 16
            maxOutputTokens = 256
        }
        generativeModel = GenerativeModel(
            generationConfig = generationConfig, downloadConfig = downloadConfig
        )
    }
}