package com.actividad1.myapplication.ui.theme.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actividad1.myapplication.data.ApiClient
import com.actividad1.myapplication.data.repository.AppSettingsRepository
import com.actividad1.myapplication.data.models.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val appSettingsRepository = AppSettingsRepository.getInstance(application)

    // Observable para el tema oscuro
    val darkModeEnabled = appSettingsRepository.darkModeEnabled

    // Estados internos
    private var _email by mutableStateOf("")

    private var _password by mutableStateOf("")

    private var _errorMessage by mutableStateOf("")

    private var _successMessage by mutableStateOf("")

    val email: String
        get() = _email

    val password: String
        get() = _password

    val errorMessage: String
        get() = _errorMessage

    val successMessage: String
        get() = _successMessage

    fun onEmailChange(newEmail: String) {
        _email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password = newPassword
    }

    // Ejecuta la lógica de login
    fun performLogin(
        onLoginSuccess: (String) -> Unit,
        onLoginError: (String) -> Unit
    ) {
        // Validar que los campos no estén vacíos
        if (_email.isEmpty() || _password.isEmpty()) {
            _errorMessage = "Por favor, completa todos los campos."
            return
        }
        // Limpiar cualquier mensaje de error previo
        _errorMessage = ""

        // Realizar la llamada a la API en un contexto de IO
        viewModelScope.launch(Dispatchers.IO) {
            val request = LoginRequest(correo = _email, password = _password)
            try {
                val response = ApiClient.apiService.login(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body()?.message ?: "Respuesta vacía"
                    withContext(Dispatchers.Main) {
                        _successMessage = responseBody
                        onLoginSuccess(responseBody)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _errorMessage = "Error al iniciar sesión: ${response.code()}"
                        onLoginError("Error al iniciar sesión: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage = "Error al conectar con el servidor: ${e.message}"
                    onLoginError("Error al conectar con el servidor: ${e.message}")
                }
            }
        }
    }
}
