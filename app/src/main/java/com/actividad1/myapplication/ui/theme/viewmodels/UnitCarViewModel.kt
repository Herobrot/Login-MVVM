package com.actividad1.myapplication.ui.theme.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actividad1.myapplication.data.models.Car
import com.actividad1.myapplication.data.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class CarStockViewModel : ViewModel() {
    private var cars by mutableStateOf<List<Car>>(emptyList())

    private var showAddEditModal by mutableStateOf(false)

    private var selectedCar by mutableStateOf<Car?>(null)

    init {
        loadCars()
    }

    private fun loadCars() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiClient.apiService.getCars().awaitResponse()
            if (response.isSuccessful) {
                response.body()?.let { fetchedCars ->
                    withContext(Dispatchers.Main) {
                        cars = fetchedCars
                    }
                }
            }
        }
    }

    fun addCar(car: Car, originalPlaca: String, onComplete: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = if (car._idKit.isEmpty()) {
                    ApiClient.apiService.createCar(car).awaitResponse()
                } else {
                    ApiClient.apiService.updateCar(originalPlaca, car).awaitResponse()
                }

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                } else {
                    // Log error details
                    println("Save Car Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Log exception
                println("Save Car Exception: ${e.message}")
            }
        }
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiClient.apiService.deleteCar(car._idKit).awaitResponse()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    loadCars()
                }
            }
        }
    }

    fun openModal(car: Car? = null) {
        selectedCar = car
        showAddEditModal = true
    }

    fun closeModal() {
        showAddEditModal = false
        selectedCar = null
    }

    fun getCars(): List<Car> { return this.cars; }
    fun getCar(): Car? { return this.selectedCar; }
    fun getModalStatus(): Boolean { return this.showAddEditModal; }
}
