package com.muhammad.data

interface GeminiNanoGenerationDataSource{
    suspend fun initialize()
    suspend fun generationPrompt(prompt : String) : String?
}
class GeminiNanoGenerationDataSourceImp(
    private val downloader: GeminiNanoDownloader
) : GeminiNanoGenerationDataSource{
    override suspend fun initialize() {
        downloader.downloadModel()
    }

    override suspend fun generationPrompt(prompt: String): String? {
        if(!downloader.isModalDownloaded()) return null
        val response = downloader.generativeModel?.generateContent(prompt)
        return response?.text
    }

}