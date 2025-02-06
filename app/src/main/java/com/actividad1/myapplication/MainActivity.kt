package com.actividad1.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.actividad1.myapplication.ui.theme.MyApplicationTheme
import com.actividad1.myapplication.ui.theme.views.LoginScreen
import com.actividad1.myapplication.ui.theme.views.CarStockScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }

}
@Composable
fun AppNavigation() {
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
        composable("carStock") { CarStockScreen(navController) }
    }
}
