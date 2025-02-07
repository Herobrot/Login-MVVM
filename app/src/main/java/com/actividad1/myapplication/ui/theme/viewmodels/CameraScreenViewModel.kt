package com.actividad1.myapplication.ui.theme.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actividad1.myapplication.data.ApiClient
import com.actividad1.myapplication.data.models.LoginImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class CameraViewModel : ViewModel() {
    fun processCapturedImage(
        bitmap: Bitmap,
        context: Context,
        onImageCaptured: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val base64Image = encodeBitmapToBase64(bitmap)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Imagen capturada", Toast.LENGTH_SHORT).show()
                }
                val request = LoginImageRequest(base64Image)
                println("Hice el request")
                val response = ApiClient.apiService.loginByImage(request).execute()
                println("Hice el response")
                if (response.isSuccessful) {
                    val responseBody = response.body()?.message ?: "Respuesta vacía"
                    println("consegui la información: $responseBody")
                    withContext(Dispatchers.Main) {
                        onImageCaptured(responseBody)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Error al iniciar sesión: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error al conectar con el servidor: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Función de ayuda para codificar un Bitmap a una cadena Base64.
    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
