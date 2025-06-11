package com.muhammad.network

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig

interface RemoteConfigDataSource{
    fun isAppInActive() : Boolean
    fun textModelName() : String
    fun imageModelName() : String
    fun promptTextVerify() : String
    fun promptImageValidation() : String
    fun promptImageDescription() : String
    fun useGeminiNano() : Boolean
    fun generateBotPrompt() : String
    fun promptImageGenerationWithSkinTone() : String
    fun getPromoVideoLink() : String
    fun getDancingDroidLink() : String
}
class RemoteConfigDataSourceImp : RemoteConfigDataSource{
    private val remoteConfig = Firebase.remoteConfig
    override fun isAppInActive(): Boolean {
        return remoteConfig.getBoolean("is_android_app_inactive")
    }

    override fun textModelName(): String {
        return remoteConfig.getString("text_model_name")
    }

    override fun imageModelName(): String {
        return remoteConfig.getString("image_model_name")
    }

    override fun promptTextVerify(): String {
        return remoteConfig.getString("prompt_text_verify")
    }

    override fun promptImageValidation(): String {
        return remoteConfig.getString("prompt_image_validation")
    }

    override fun promptImageDescription(): String {
        return remoteConfig.getString("prompt_image_description")
    }

    override fun useGeminiNano(): Boolean {
        return remoteConfig.getBoolean("use_gemini_nano")
    }

    override fun generateBotPrompt(): String {
        return remoteConfig.getString("generate_bot_prompt")
    }

    override fun promptImageGenerationWithSkinTone(): String {
        return remoteConfig.getString("prompt_image_generation_skin_tone")
    }

    override fun getPromoVideoLink(): String {
        return remoteConfig.getString("promo_video_link")
    }

    override fun getDancingDroidLink(): String {
        return remoteConfig.getString("dancing_droid_gif_link")
    }

}