package com.muhammad.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.muhammad.network.model.ValidatedDescription
import com.muhammad.network.model.ValidatedImage
import com.muhammad.network.vertexai.FirebaseAIDataSource
import com.muhammad.util.LocalFileProvider
import java.io.File
import java.util.UUID

interface ImageGenerationRepository {
    suspend fun initialize()
    suspend fun generateFromDescription(description: String, skinTone: String): Bitmap
    suspend fun generateFromImage(file: File, skinTone: String): Bitmap
    suspend fun saveImage(imageBitmap: Bitmap): Uri
    suspend fun saveImageToExternalStorage(imageBitmap: Bitmap): Uri
    suspend fun saveImageToExternalStorage(imageUri: Uri): Uri
}

class ImageGenerationRepositoryImp(
    val localFileProvider: LocalFileProvider,
    val internetConnectivityManager: InternetConnectivityManager,
    val geminiNanoGenerationDataSource: GeminiNanoGenerationDataSource,
    val firebaseAIDataSource: FirebaseAIDataSource,
) : ImageGenerationRepository {
    override suspend fun initialize() {
        geminiNanoGenerationDataSource.initialize()
    }
    @Throws(InsufficientInformationException::class)
    override suspend fun generateFromDescription(
        description: String,
        skinTone: String,
    ) : Bitmap{
        checkInternetConnection()
        if(description.isBlank()) throw InsufficientInformationException()
        val validatedPrompt = validatePromptHasEnoughInformation(description)
        if(!validatedPrompt.success || validatedPrompt.userDescription == null){
            throw InsufficientInformationException()
        }
        return firebaseAIDataSource.generateImageFromPromptAndSkinTone(
            prompt =validatedPrompt.userDescription.toString(), skinTone = skinTone
        )
    }

    override suspend fun generateFromImage(
        file: File,
        skinTone: String,
    ): Bitmap {
        checkInternetConnection()
        val validatedImage = validateImageIsFullPerson(file)
        if (!validatedImage.success) {
            throw ImageValidationException(validatedImage.errorMessage?.toImageValidationError())
        }
        val imageDescription = firebaseAIDataSource.generateDescriptivePromptFromImage(BitmapFactory.decodeFile(file.absolutePath))
        if(!imageDescription.success || imageDescription.userDescription == null){
            throw ImageDescriptionFailedGenerationException()
        }
        return firebaseAIDataSource.generateImageFromPromptAndSkinTone(
            imageDescription.userDescription.toString(),skinTone
        )
    }

    override suspend fun saveImage(imageBitmap: Bitmap): Uri {
        val cacheFile = localFileProvider.createCacheFile("shared_image_${UUID.randomUUID()}.jpg")
        val file = localFileProvider.saveBitmapToFile(imageBitmap, cacheFile)
        return localFileProvider.sharingUriForFile(file)
    }

    override suspend fun saveImageToExternalStorage(imageBitmap: Bitmap): Uri {
        val cacheFile =
            localFileProvider.createCacheFile("pro_image_result_${UUID.randomUUID()}.jpg")
        localFileProvider.saveBitmapToFile(bitmap = imageBitmap, file = cacheFile)
        return localFileProvider.saveToSharedStorage(
            file = cacheFile,
            fileName = cacheFile.name,
            mimeType = "image/jpeg"
        )
    }

    override suspend fun saveImageToExternalStorage(imageUri: Uri): Uri {
        return localFileProvider.saveUriToSharedStorage(
            inputUri = imageUri,
            fileName = "pro_image_original_${UUID.randomUUID()}.jpg",
            "image/jpeg"
        )
    }

    private fun checkInternetConnection() {
        if (!internetConnectivityManager.isInternetAvailable()) {
            throw NoInternetException()
        }
    }

    private suspend fun validateImageIsFullPerson(file: File): ValidatedImage =
        firebaseAIDataSource.validateImageHasEnoughInformation(
            BitmapFactory.decodeFile(
                file.absolutePath
            )
        )

    private suspend fun validatePromptHasEnoughInformation(prompt: String): ValidatedDescription =
        firebaseAIDataSource.validatePromptHasEnoughInformation(prompt)
}