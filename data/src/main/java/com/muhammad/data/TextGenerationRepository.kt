package com.muhammad.data

import com.muhammad.network.RemoteConfigDataSource
import com.muhammad.network.vertexai.FirebaseAIDataSource

interface TextGenerationRepository {
    suspend fun initialize()
    suspend fun getNextGeneratedBotPrompt(): String?
}

class TextGenerationRepositoryImp(
    private val remoteConfigDataSource: RemoteConfigDataSource,
    private val geminiNanoGenerationDataSource: GeminiNanoGenerationDataSource,
    private val firebaseAIDataSource: FirebaseAIDataSource,
) : TextGenerationRepository {
    private var currentPrompts: List<String>? = null
    private var currentPromptIndex = 0
    override suspend fun initialize() {
        geminiNanoGenerationDataSource.initialize()
    }
    override suspend fun getNextGeneratedBotPrompt(): String? {
        val prompts = currentPrompts
        if(prompts.isNullOrEmpty() || currentPromptIndex >= prompts.size){
            currentPrompts = generateBotPrompts()
            if(currentPrompts.isNullOrEmpty()){
                return null
            } else{
                return getNextGeneratedBotPrompt()
            }
        }
        val currentPrompt = prompts[currentPromptIndex]
        currentPromptIndex++
        return currentPrompt
    }
    private suspend fun generateBotPrompts() : List<String>{
        val prompt = remoteConfigDataSource.generateBotPrompt()
        currentPromptIndex = 0
        val nanoResult = if(remoteConfigDataSource.useGeminiNano()){
            geminiNanoGenerationDataSource.generationPrompt(prompt)
        } else null
        if(nanoResult.isNullOrEmpty()){
            val result = firebaseAIDataSource.generatePrompt(prompt).generatedPrompts
            return result ?: emptyList()
        } else{
            return listOf(nanoResult)
        }
    }
}