package com.muhammad.results

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammad.data.ImageGenerationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultViewModel(
    private val imageGenerationRepository: ImageGenerationRepository,
) : ViewModel() {
    private val ioDispatcher = Dispatchers.IO
    private val _state = MutableStateFlow(ResultState())
    val state = _state.asStateFlow()
    private var _snackbarHostState = MutableStateFlow(SnackbarHostState())
    val snackbarHostState = _snackbarHostState.asStateFlow()
    fun setArguments(resultImageUrl: Bitmap, originalImageUrl: Uri?, promptText: String?) {
        _state.update {
            it.copy(
                resultImageBitmap = resultImageUrl,
                originalImageUrl = originalImageUrl,
                promptText = promptText ?:""
            )
        }
    }

    fun shareClick() {
        viewModelScope.launch(ioDispatcher) {
            val resultUrl = state.value.resultImageBitmap
            if (resultUrl != null) {
                val imageFileUri = imageGenerationRepository.saveImage(resultUrl)
                _state.update {
                    it.copy(saveUri = imageFileUri)
                }
            }
        }
    }
    fun downloadClicked(){
        viewModelScope.launch(ioDispatcher){
            val resultBitmap = state.value.resultImageBitmap
            val originalImage = state.value.originalImageUrl
            if(originalImage != null){
                val savedOriginalUri = imageGenerationRepository.saveImageToExternalStorage(originalImage)
                _state.update { it.copy(externalOriginalUrl = savedOriginalUri) }
            }
            if(resultBitmap != null){
                val imageUri = imageGenerationRepository.saveImageToExternalStorage(resultBitmap)
                _state.update { it.copy(externalSavedUrl = imageUri) }
                snackbarHostState.value.showSnackbar("Download completed.")
            }
        }
    }
}

data class ResultState(
    val resultImageBitmap: Bitmap? = null,
    val originalImageUrl: Uri? = null,
    val saveUri: Uri? = null,
    val externalSavedUrl: Uri? = null,
    val externalOriginalUrl: Uri? = null,
    val promptText: String = "a Nice Dress with a Mop",
)