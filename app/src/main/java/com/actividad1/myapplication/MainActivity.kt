package com.actividad1.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.actividad1.myapplication.data.AppSettingsRepository
import com.actividad1.myapplication.ui.theme.views.CameraScreen
import com.actividad1.myapplication.ui.theme.views.CarStockScreen
import com.actividad1.myapplication.ui.theme.views.LoginScreen
import com.actividad1.myapplication.ui.theme.views.SettingsScreen
import com.actividad1.myapplication.ui.theme.MyApplicationTheme
import com.actividad1.myapplication.ui.theme.viewmodels.ViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var appSettingsRepository: AppSettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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