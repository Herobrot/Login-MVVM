package com.actividad1.myapplication.ui.theme.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actividad1.myapplication.data.models.Car
import com.actividad1.myapplication.data.ApiClient
import com.actividad1.myapplication.data.AppSettingsRepository
import com.actividad1.myapplication.data.models.NewCar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class UnitCarViewModel(application: Application) : AndroidViewModel(application) {

    private val appSettingsRepository = AppSettingsRepository.getInstance(application)

    // Observable para el tema oscuro
    val darkModeEnabled = appSettingsRepository.darkModeEnabled
    // Clase para representar el estado de notificación
    data class NotificationState(
        val message: String = "",
        val type: NotificationType = NotificationType.NONE,
        val isVisible: Boolean = false
    )

    // Tipos de notificación
    enum class NotificationType {
        SUCCESS, ERROR, WARNING, INFO, NONE
    }

    // Estados internos privados
    private var _cars by mutableStateOf<List<Car>>(emptyList())
    private var _showAddEditModal by mutableStateOf(false)
    private var _selectedCar by mutableStateOf<Car?>(null)
    private var _notification by mutableStateOf(NotificationState())

    // Propiedades públicas de solo lectura para exponer el estado
    val cars: List<Car>
        get() = _cars

    val modalStatus: Boolean
        get() = _showAddEditModal

    val selectedCar: Car?
        get() = _selectedCar

    val notification: NotificationState
        get() = _notification

    init {
        loadCars()
    }

    private fun loadCars() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiClient.apiService.getCars().awaitResponse()
            if (response.isSuccessful) {
                response.body()?.let { fetchedCars ->
                    withContext(Dispatchers.Main) {
                        _cars = fetchedCars
                    }
                }
            }
        }
    }

    fun addCar(car: Car, originalPlaca: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = if (car._id.isEmpty()) {
                    println("entre en ID porque NO TIENE")
                    val auxiliarCar = NewCar(placa = car.placa, modelo = car.modelo, chofer = car.chofer, activo = car.activo)
                    ApiClient.apiService.createCar(auxiliarCar).awaitResponse()
                } else {
                    println("entre con ID, placaOriginal = $originalPlaca, placa = ${car.placa}")
                    val auxiliarCar = NewCar(placa = car.placa, modelo = car.modelo, chofer = car.chofer, activo = car.activo)
                    ApiClient.apiService.updateCar(originalPlaca, auxiliarCar).awaitResponse()
                }

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        savedCar()
                    }
                } else {
                    // Registrar error
                    println("Save Car Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Registrar excepción
                println("Save Car Exception: ${e.message}")
            }
        }
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch(Dispatchers.IO) {
            if (car._id.isEmpty()){
                println("No se selecciono un carro")
            }
            else{
                val response = ApiClient.apiService.deleteCar(car._id).awaitResponse()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        loadCars()
                    }
                }
            }
        }
    }

    private fun savedCar() {
        loadCars()
        _showAddEditModal = false
    }

    fun openModal(car: Car? = null) {
        _selectedCar = car
        _showAddEditModal = true
    }

    fun closeModal() {
        _showAddEditModal = false
        _selectedCar = null
    }

    private fun showNotification(message: String, type: NotificationType) {
        _notification = NotificationState(message = message, type = type, isVisible = true)

        // Configurar un temporizador para ocultar la notificación después de un tiempo
        viewModelScope.launch {
            delay(3000) // Duración de la notificación: 3 segundos
            _notification = _notification.copy(isVisible = false)
        }
    }

    // Metodo para cerrar la notificación manualmente
    fun dismissNotification() {
        _notification = _notification.copy(isVisible = false)
    }
}
