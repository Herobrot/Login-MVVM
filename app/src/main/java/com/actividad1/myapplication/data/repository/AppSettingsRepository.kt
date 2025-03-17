package com.actividad1.myapplication.data.repository

import android.content.Context
import com.actividad1.myapplication.security.EncryptedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppSettingsRepository private constructor(context: Context) {

    private val encryptedPreferences = EncryptedPreferences.getInstance(context)

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

        @Volatile
        private var INSTANCE: AppSettingsRepository? = null

        fun getInstance(context: Context): AppSettingsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppSettingsRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // StateFlow para que los cambios de configuración se propaguen a todos los observadores
    private val _darkModeEnabled = MutableStateFlow(isDarkModeEnabled())
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(areNotificationsEnabled())
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    // Métodos para acceder y modificar la configuración

    fun isDarkModeEnabled(): Boolean {
        return encryptedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        encryptedPreferences.putBoolean(KEY_DARK_MODE, enabled)
        _darkModeEnabled.value = enabled
    }

    fun areNotificationsEnabled(): Boolean {
        return encryptedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        encryptedPreferences.putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
        _notificationsEnabled.value = enabled
    }
}