package com.actividad1.myapplication

import android.os.Build
import android.os.Bundle
import android.Manifest
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.actividad1.myapplication.data.repository.AppSettingsRepository
import com.actividad1.myapplication.ui.theme.views.CameraScreen
import com.actividad1.myapplication.ui.theme.views.CarStockScreen
import com.actividad1.myapplication.ui.theme.views.LoginScreen
import com.actividad1.myapplication.ui.theme.views.SettingsScreen
import com.actividad1.myapplication.ui.theme.MyApplicationTheme
import com.actividad1.myapplication.ui.theme.viewmodels.ViewModelFactory
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private lateinit var appSettingsRepository: AppSettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // Obtener el token de Firebase y mostrarlo en el log
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM", "Error al obtener el token", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "Token Firebase: $token")
        }

        // Suscribirse al tópico "notifications"
        FirebaseMessaging.getInstance().subscribeToTopic("notifications")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM", "Error al suscribirse al tópico", task.exception)
                } else {
                    Log.d("FCM", "Suscripción exitosa al tópico 'notifications'")
                }
            }

        // Inicializar el repositorio
        appSettingsRepository = AppSettingsRepository.getInstance(applicationContext)

        // Configurar ViewModelFactory
        val viewModelFactory = ViewModelFactory(application)

        setContent {
            // Obtener el valor actual del tema oscuro
            val darkModeEnabled by appSettingsRepository.darkModeEnabled.collectAsState()

            MyApplicationTheme(darkTheme = darkModeEnabled) {
                AppNavigation(viewModelFactory)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModelFactory: ViewModelFactory) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("carStock")
                },
                onLoginError = { errorMessage ->
                    println("Error: $errorMessage")
                }
            )
        }
        composable("carStock") {
            CarStockScreen(navController, viewModelFactory)
        }
        composable("camera") {
            CameraScreen(
                onImageCaptured = { imageBase64 ->
                    navController.navigate("carStock")
                }
            )
        }
        composable("settings") {
            SettingsScreen(navController, viewModelFactory)
        }
    }
}