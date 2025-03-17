package com.actividad1.myapplication.security;

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Wrapper para SharedPreferences encriptadas usando la biblioteca de seguridad de Android
 */
class EncryptedPreferences private constructor(context: Context) {

    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val encryptedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "app_settings",
        masterKey,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getString(key: String, defaultValue: String = ""): String {
        return encryptedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun putString(key: String, value: String) {
        encryptedPreferences.edit().putString(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return encryptedPreferences.getBoolean(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        encryptedPreferences.edit().putBoolean(key, value).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: EncryptedPreferences? = null

        fun getInstance(context: Context): EncryptedPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EncryptedPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}