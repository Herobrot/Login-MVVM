package com.actividad1.myapplication.data.repository

import android.util.Log
import com.actividad1.myapplication.data.ApiClient
import com.actividad1.myapplication.data.models.Car
import com.actividad1.myapplication.data.models.NewCar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CarRepository {

    private val apiService = ApiClient.apiService

    suspend fun getAllCars(): List<Car> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCars().awaitResponse()
            return@withContext response
        } catch (e: Exception) {
            Log.e("CarRepository", "Error fetching cars", e)
            return@withContext emptyList()
        }
    }

    suspend fun insertCar(car: Car) = withContext(Dispatchers.IO) {
        // Convertimos el Car a NewCar para la API
        val newCar = NewCar(
            placa = car.placa,
            modelo = car.modelo,
            chofer = car.chofer,
            activo = car.activo
        )
        apiService.createCar(newCar).awaitResponse()
    }

    suspend fun createCar(newCar: NewCar): Car = withContext(Dispatchers.IO) {
        return@withContext apiService.createCar(newCar).awaitResponse()
    }

    suspend fun updateCar(car: Car) = withContext(Dispatchers.IO) {
        // Convertimos el Car a NewCar para la API
        val newCar = NewCar(
            placa = car.placa,
            modelo = car.modelo,
            chofer = car.chofer,
            activo = car.activo
        )
        apiService.updateCar(car.placa, newCar).awaitResponse()
    }

    suspend fun deleteCar(car: Car) = withContext(Dispatchers.IO) {
        apiService.deleteCar(car._id).awaitResponse()
    }


    private suspend fun <T> Call<T>.awaitResponse(): T {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                cancel()
            }

            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            continuation.resume(it)
                        } ?: continuation.resumeWithException(
                            NullPointerException("Response body is null")
                        )
                    } else {
                        continuation.resumeWithException(
                            Exception("API call failed with error code: ${response.code()}")
                        )
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    companion object {
        val instance: CarRepository by lazy { CarRepository() }
    }
}