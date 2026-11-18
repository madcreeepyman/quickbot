package com.snacky.quickbot

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePrefs {
    private const val FILE_NAME = "secure_settings"
    private const val KEY_API = "openrouter_api_key"
    private const val KEY_MODEL = "openrouter_model"
    private const val KEY_STREAMING = "streaming_enabled"

    private fun prefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveKey(context: Context, key: String) {
        prefs(context).edit().putString(KEY_API, key).apply()
    }

    fun getKey(context: Context): String? =
        prefs(context).getString(KEY_API, null)

    fun clearKey(context: Context) {
        prefs(context).edit().remove(KEY_API).apply()
    }

    fun getModel(context: Context): String =
        prefs(context).getString(KEY_MODEL, null) ?: "openai/gpt-4o-mini"

    fun saveModel(context: Context, model: String) {
        prefs(context).edit().putString(KEY_MODEL, model).apply()
    }

    fun isStreamingEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_STREAMING, false)

    fun setStreamingEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_STREAMING, enabled).apply()
    }
}