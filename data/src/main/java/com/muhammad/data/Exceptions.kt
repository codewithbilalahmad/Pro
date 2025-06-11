package com.muhammad.data

import com.muhammad.network.model.ImageValidationError as ModelImageValidationError

class InsufficientInformationException(errorMessage : String?=null) : Exception(errorMessage)
class ImageValidationException(val imageValidationError: ImageValidationError?=null) : Exception(imageValidationError.toString())
class ImageDescriptionFailedGenerationException() : Exception()
class NoInternetException : Exception("No Internet connection")

enum class ImageValidationError{
    NOT_PERSON,
    NOT_ENOUGH_DETAIL,
    POLICY_VIOLATION,
    OTHER
}
fun ModelImageValidationError.toImageValidationError() : ImageValidationError{
    return when(this){
        ModelImageValidationError.NO_PERSON -> ImageValidationError.NOT_PERSON
        ModelImageValidationError.NO_ENOUGH_DETAIL -> ImageValidationError.NOT_ENOUGH_DETAIL
        ModelImageValidationError.POLICY_VIOLATION -> ImageValidationError.POLICY_VIOLATION
        ModelImageValidationError.OTHER -> ImageValidationError.OTHER
    }
}
