package com.muhammad.creation

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammad.data.ImageDescriptionFailedGenerationException
import com.muhammad.data.ImageGenerationRepository
import com.muhammad.data.ImageValidationError
import com.muhammad.data.ImageValidationException
import com.muhammad.data.InsufficientInformationException
import com.muhammad.data.InternetConnectivityManager
import com.muhammad.data.NoInternetException
import com.muhammad.data.TextGenerationRepository
import com.muhammad.util.LocalFileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreationViewModel(
    private val context: Context,
    private val internetConnectivityManager: InternetConnectivityManager,
    private val imageGenerationRepository: ImageGenerationRepository,
    private val textGenerationRepository: TextGenerationRepository,
    private val fileProvider: LocalFileProvider,
) : ViewModel() {
    init {
        viewModelScope.launch {
            imageGenerationRepository.initialize()
            textGenerationRepository.initialize()
        }
    }
    private val ioDispatcher = Dispatchers.IO
    private val _state = MutableStateFlow(CreationState())
    val state = _state.asStateFlow()
    private val _snackbarHostState = MutableStateFlow(SnackbarHostState())
    val snackbarHostState = _snackbarHostState.asStateFlow()
    fun onImageSelected(uri: Uri?) {
        _state.update {
            it.copy(imageUri = uri, selectedPromptType = PromptType.PHOTO)
        }
    }
    fun onBotColorChange(color: BotColor) {
        _state.update { it.copy(botColor = color) }
    }

    fun onSelectedPromptOptionChange(promptType: PromptType) {
        _state.update { it.copy(selectedPromptType = promptType) }
    }

    fun onPromptGenerationClick() {
        viewModelScope.launch(ioDispatcher) {
            _state.update { it.copy(promptGenerationInProgress = true) }
            try {
                val prompt = textGenerationRepository.getNextGeneratedBotPrompt()
                if (prompt != null) {
                    _state.update {
                        it.copy(generatedPrompt = prompt, promptGenerationInProgress = false)
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(promptGenerationInProgress = false)
                }
            }
        }
    }

    fun startClicked() {
        viewModelScope.launch(ioDispatcher) {
            if (internetConnectivityManager.isInternetAvailable()) {
                try {
                    val bitmap = when (state.value.selectedPromptType) {
                        PromptType.PHOTO -> {
                            val selectedImage = _state.value.imageUri
                            if (selectedImage == null) {
                                _state.update { it.copy(screenState = ScreenState.EDIT) }
                                _snackbarHostState.value.showSnackbar(context.getString(R.string.error_choose_image_prompt))
                                return@launch
                            } else {
                                imageGenerationRepository.generateFromImage(
                                    fileProvider.copyToInternalStorage(
                                        selectedImage
                                    ), state.value.botColor.getVerboseDescription()
                                )
                            }
                        }

                        PromptType.TEXT -> {
                            imageGenerationRepository.generateFromDescription(
                                _state.value.descriptionText.toString(),
                                _state.value.botColor.getVerboseDescription()
                            )
                        }
                    }
                } catch (e: Exception) {
                    handleImageGenerationError(e)
                }
            } else {
                displayNoInternet()
            }
        }
    }

    private suspend fun handleImageGenerationError(exception: Exception) {
        _state.update {
            it.copy(screenState = ScreenState.EDIT)
        }
        val message = when (exception) {
            is ImageValidationException -> {
                when (exception.imageValidationError) {
                    ImageValidationError.NOT_PERSON -> context.getString(R.string.error_image_generation_full_body)
                    ImageValidationError.NOT_ENOUGH_DETAIL -> context.getString(R.string.error_image_generation_detailed_description)
                    ImageValidationError.POLICY_VIOLATION -> context.getString(R.string.error_image_generation_policy_violation)
                    ImageValidationError.OTHER -> context.getString(R.string.error_image_generation_other)
                    null -> context.getString(R.string.error_image_generation_other)
                }
            }

            is InsufficientInformationException -> context.getString(R.string.error_provide_more_descriptive_bot)
            is NoInternetException -> context.getString(R.string.error_connectivity)
            is ImageDescriptionFailedGenerationException -> context.getString(R.string.error_image_validation)
            else -> {
                context.getString(R.string.error_upload_generic)
            }
        }
        _snackbarHostState.value.showSnackbar(message)
    }

    private suspend fun displayNoInternet() {
        _state.update {
            it.copy(screenState = ScreenState.EDIT)
        }
        _snackbarHostState.value.showSnackbar(context.getString(R.string.error_no_internet))
    }

    private fun cancelInProgressTask() {
        ioDispatcher.cancel()
        _state.update {
            it.copy(screenState = ScreenState.EDIT)
        }
    }

    fun onUndoPressed() {
        _state.update { it.copy(imageUri = null) }
    }

     fun onBackPressed() {
        when (state.value.screenState) {
            ScreenState.EDIT -> {}
            ScreenState.LOADING -> {
                cancelInProgressTask()
            }

            ScreenState.RESULT -> {
                _state.update { it.copy(screenState = ScreenState.EDIT, resultBitmap = null) }
            }
        }
    }
}

data class CreationState(
    val selectedPromptType: PromptType = PromptType.PHOTO,
    val listBotColors: List<BotColor> = getBotColors(),
    val botColor: BotColor = listBotColors.first(),
    val imageUri: Uri? = null,
    val descriptionText: TextFieldState = TextFieldState(),
    val generatedPrompt: String? = null,
    val promptGenerationInProgress: Boolean = false,
    val screenState: ScreenState = ScreenState.EDIT,
    val resultBitmap: Bitmap? = null,
)

enum class ScreenState {
    EDIT, LOADING, RESULT
}

data class BotColor(
    val name: String, val value: String, val imageRes: Int? = null,
    val color: Color? = null,
) {
    fun getVerboseDescription(): String {
        return "$name ($value)"
    }
}

private fun getBotColors(): List<BotColor> {
    return listOf(
        BotColor("Green", "#50C168", color = Color(0xFF50C168)),
        BotColor("Light Almond", "#F1DFD4", color = Color(0xFFF1DFD4)),
        BotColor("Light Champagne", "#F3E0CF", color = Color(0xFFF3E0CF)),
        BotColor("Wheat", "#F2DBBB", color = Color(0xFFF2DBBB)),
        BotColor("Birch Beige", "#DABE9B", color = Color(0xFFDABE9B)),
        BotColor("Tan", "#BD9A71", color = Color(0xFFBD9A71)),
        BotColor("Coyote Brown", "#8A633F", color = Color(0xFF8A633F)),
        BotColor("Chocolate", "#784C38", color = Color(0xFF784C38)),
        BotColor("Syrup Brown", "#633A2E", color = Color(0xFF633A2E)),
        BotColor("Espresso", "#45332D", color = Color(0xFF45332D)),
        BotColor("Black Brown", "#2C2523", color = Color(0xFF2C2523)),
        BotColor("Hot Pink", "#DB79D7", color = Color(0xFFDB79D7)),
        BotColor("Ultra Purple", "#9C6CD5", color = Color(0xFF9C6CD5)),
        BotColor("Honey Yellow", "#E2C96C", color = Color(0xFFE2C96C)),
        BotColor("Light Pink", "#E0BFC3", color = Color(0xFFE0BFC3)),
        BotColor("Flame Orange", "#DB774A", color = Color(0xFFDB774A)),
        BotColor("Tangerine", "#DC944F", color = Color(0xFFDC944F)),
        BotColor("Ocean Blue", "#5090D5", color = Color(0xFF5090D5)),
        BotColor("Cloud Gray", "#CBCBCB", color = Color(0xFFCBCBCB)),
    )
}

enum class PromptType(val displayName: String) {
    PHOTO("Photo"),
    TEXT("Prompt")
}