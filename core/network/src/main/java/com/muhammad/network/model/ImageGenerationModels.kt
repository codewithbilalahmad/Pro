package com.muhammad.network.model

import androidx.annotation.Keep

@Keep
data class ValidatedDescription(
    val success : Boolean,
    val userDescription : String?
)
@Keep
data class ValidatedImage(
    val success : Boolean,
    val errorMessage : ImageValidationError?
)
@Keep()
data class GeneratedPrompt(
    val success: Boolean,
    val generatedPrompts : List<String>?
)
@Keep
enum class ImageValidationError(val description : String){
    NO_PERSON("not_a_person"),
    NO_ENOUGH_DETAIL("not_enough_detail"),
    POLICY_VIOLATION("policy_violation"),
    OTHER("other")
}