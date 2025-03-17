package com.actividad1.myapplication.ui.theme.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.actividad1.myapplication.ui.theme.viewmodels.UnitCarViewModel
import com.actividad1.myapplication.ui.theme.SettingsViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(application) as T
            }
            modelClass.isAssignableFrom(UnitCarViewModel::class.java) -> {
                UnitCarViewModel(application) as T
            }
            // Añadir aquí otros ViewModels
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}