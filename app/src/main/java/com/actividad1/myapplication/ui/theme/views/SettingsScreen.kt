package com.actividad1.myapplication.ui.theme.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.actividad1.myapplication.ui.theme.viewmodels.SettingsViewModel
import com.actividad1.myapplication.ui.theme.viewmodels.ViewModelFactory

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModelFactory: ViewModelFactory
) {
    // Obtener el ViewModel utilizando el Factory
    val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)

    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Opción de Tema Oscuro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tema Oscuro")
            Switch(
                checked = darkModeEnabled,
                onCheckedChange = { viewModel.toggleDarkMode() }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Opción de Notificaciones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Notificaciones")
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { viewModel.toggleNotifications() }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Más opciones de configuración...

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Guardar")
        }
    }
}