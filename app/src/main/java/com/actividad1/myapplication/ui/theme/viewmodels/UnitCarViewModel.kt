package com.actividad1.myapplication.ui.theme.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actividad1.myapplication.data.models.Car
import com.actividad1.myapplication.data.models.NewCar
import com.actividad1.myapplication.data.repository.AppSettingsRepository
import com.actividad1.myapplication.data.repository.CarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UnitCarViewModel(application: Application) : AndroidViewModel(application) {

    // Repositorios
    private val appSettingsRepository = AppSettingsRepository.getInstance(application)
    private val carRepository = CarRepository.instance

    // Estado observable para el tema oscuro
    val darkModeEnabled = appSettingsRepository.darkModeEnabled

    // Estado para la lista de coches
    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    // Estado para el modal
    private val _modalStatus = MutableStateFlow(false)
    val modalStatus: StateFlow<Boolean> = _modalStatus.asStateFlow()

    // Estado para el coche seleccionado para editar
    private val _selectedCar = MutableStateFlow<Car?>(null)
    val selectedCar: StateFlow<Car?> = _selectedCar.asStateFlow()

    init {
        loadCars()
    }

    private fun loadCars() {
        viewModelScope.launch {
            try {
                _cars.value = carRepository.getAllCars()
            } catch (e: Exception) {
                Log.e("UnitCarViewModel", "Error loading cars", e)
            }
        }
    }

    fun openModal(car: Car? = null) {
        _selectedCar.value = car
        _modalStatus.value = true
    }

    fun closeModal() {
        _selectedCar.value = null
        _modalStatus.value = false
    }

    fun addCar(car: NewCar) {
        viewModelScope.launch {
            carRepository.createCar(car)
            loadCars()
        }
    }

    fun updateCar(car: Car) {
        viewModelScope.launch {
            carRepository.updateCar(car)
            loadCars()
        }
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch {
            carRepository.deleteCar(car)
            loadCars()
        }
    }
}
