package com.actividad1.myapplication.ui.theme.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actividad1.myapplication.data.repository.AppSettingsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val appSettingsRepository = AppSettingsRepository.getInstance(application)

    val darkModeEnabled: StateFlow<Boolean> = appSettingsRepository.darkModeEnabled
    val notificationsEnabled: StateFlow<Boolean> = appSettingsRepository.notificationsEnabled

    fun toggleDarkMode() {
        viewModelScope.launch {
            appSettingsRepository.setDarkModeEnabled(!appSettingsRepository.isDarkModeEnabled())
        }
    }

    fun toggleNotifications() {
        viewModelScope.launch {
            appSettingsRepository.setNotificationsEnabled(!appSettingsRepository.areNotificationsEnabled())
        }
    }
}