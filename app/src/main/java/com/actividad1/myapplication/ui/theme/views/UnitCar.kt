package com.actividad1.myapplication.ui.theme.views

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.actividad1.myapplication.data.models.Car
import com.actividad1.myapplication.ui.theme.MyApplicationTheme
import com.actividad1.myapplication.ui.theme.viewmodels.UnitCarViewModel
import com.google.firebase.messaging.FirebaseMessaging

class UnitCar : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicitar permiso para notificaciones en Android 13 (TIRAMISU) o superior
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


        // Configurar la UI con Jetpack Compose
        setContent {
            MyApplicationTheme {  }
        }
    }
}

@Composable
fun CarStockScreen(navController: NavController, viewModel: UnitCarViewModel = viewModel()) {

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text(
            text = "Stock de Carros",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { viewModel.openModal() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Agregar Carro")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(viewModel.cars.size) { index ->
                val car = viewModel.cars[index]
                CarItem(
                    car = car,
                    onEdit = { viewModel.openModal(car) },
                    onDelete = { viewModel.deleteCar(car) }
                )
            }
        }

        if (viewModel.modalStatus) {
            AddEditCarModal(
                onDismiss = { viewModel.closeModal() },
                viewModel = viewModel
            )
        }

    }
}

@Composable
fun CarItem(car: Car, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Placa: ${car.placa}")
            Text(text = "Modelo: ${car.modelo}")
            Text(text = "Chofer: ${car.chofer}")
            Text(text = "Activo: ${if (car.activo) "Sí" else "No"}")

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onEdit, modifier = Modifier.padding(end = 8.dp)) {
                    Text("Editar")
                }
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCarModal(
    viewModel: UnitCarViewModel,
    onDismiss: () -> Unit
) {
    var placa by remember { mutableStateOf(viewModel.selectedCar?.placa ?: "") }
    var originalPlaca by remember { mutableStateOf(viewModel.selectedCar?.placa ?: "") }
    var modelo by remember { mutableStateOf(viewModel.selectedCar?.modelo ?: "") }
    var chofer by remember { mutableStateOf(viewModel.selectedCar?.chofer ?: "") }
    var activo by remember { mutableStateOf(viewModel.selectedCar?.activo ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (viewModel.selectedCar == null) "Agregar Carro" else "Editar Carro") },
        text = {
            Column {
                // Campo de texto para "Placa"
                BasicTextField(
                    value = placa,
                    onValueChange = { placa = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        OutlinedTextFieldDefaults.DecorationBox(
                            value = placa,
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = remember { MutableInteractionSource() },
                            isError = false,
                            label = { Text("Placa") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            contentPadding = PaddingValues(8.dp),
                            container = {
                                OutlinedTextFieldDefaults.ContainerBox(
                                    enabled = true,
                                    isError = false,
                                    interactionSource = remember { MutableInteractionSource() },
                                    shape = MaterialTheme.shapes.small,
                                    colors = TextFieldDefaults.colors()
                                )
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Campo de texto para "Modelo"
                BasicTextField(
                    value = modelo,
                    onValueChange = { modelo = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        OutlinedTextFieldDefaults.DecorationBox(
                            value = modelo,
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = remember { MutableInteractionSource() },
                            isError = false,
                            label = { Text("Modelo") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            contentPadding = PaddingValues(8.dp),
                            container = {
                                OutlinedTextFieldDefaults.ContainerBox(
                                    enabled = true,
                                    isError = false,
                                    interactionSource = remember { MutableInteractionSource() },
                                    shape = MaterialTheme.shapes.small,
                                    colors = TextFieldDefaults.colors()
                                )
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Campo de texto para "Chofer"
                BasicTextField(
                    value = chofer,
                    onValueChange = { chofer = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        OutlinedTextFieldDefaults.DecorationBox(
                            value = chofer,
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = remember { MutableInteractionSource() },
                            isError = false,
                            label = { Text("Chofer") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            contentPadding = PaddingValues(8.dp),
                            container = {
                                OutlinedTextFieldDefaults.ContainerBox(
                                    enabled = true,
                                    isError = false,
                                    interactionSource = remember { MutableInteractionSource() },
                                    shape = MaterialTheme.shapes.small,
                                    colors = TextFieldDefaults.colors()
                                )
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Activo:")
                    Checkbox(checked = activo, onCheckedChange = { activo = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newCar = Car(placa, modelo, chofer, activo, viewModel.selectedCar?._idKit ?: "", viewModel.selectedCar?._id ?: "")
                    println(newCar)
                    viewModel.addCar(newCar, originalPlaca)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
