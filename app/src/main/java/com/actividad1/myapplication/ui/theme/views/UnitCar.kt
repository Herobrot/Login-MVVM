package com.actividad1.myapplication.ui.theme.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.actividad1.myapplication.data.models.Car
import com.actividad1.myapplication.data.models.NewCar
import com.actividad1.myapplication.ui.theme.components.ConfirmDialog
import com.actividad1.myapplication.ui.theme.viewmodels.UnitCarViewModel
import com.actividad1.myapplication.ui.theme.viewmodels.ViewModelFactory

@Composable
fun CarStockScreen(
    navController: NavController,
    viewModelFactory: ViewModelFactory
) {
    // Obtener el ViewModel utilizando el Factory
    val viewModel: UnitCarViewModel = viewModel(factory = viewModelFactory)

    // Estados observables
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
    val cars by viewModel.cars.collectAsState()
    val modalStatus by viewModel.modalStatus.collectAsState()
    val selectedCar by viewModel.selectedCar.collectAsState()

    // Estado para mostrar diálogo de confirmación de eliminación
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var carToDelete by remember { mutableStateOf<Car?>(null) }

    Scaffold(
        topBar = {
            CarStockTopBar()
        },
        floatingActionButton = {
            AddCarFab(onClick = { viewModel.openModal() })
        }
    ) { paddingValues ->
        CarStockContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cars = cars,
            onEditCar = { car -> viewModel.openModal(car) },
            onDeleteCar = { car ->
                carToDelete = car
                showDeleteConfirmation = true
            }
        )

        // Modal para agregar/editar carro
        if (modalStatus) {
            AddEditCarDialog(
                car = selectedCar,
                onDismiss = { viewModel.closeModal() },
                onSave = { car ->
                    if (selectedCar != null) {
                        viewModel.updateCar(car)
                    } else {
                        viewModel.addCar(NewCar(placa = car.placa, modelo = car.modelo, chofer = car.chofer, activo = car.activo))
                    }
                    viewModel.closeModal()
                }
            )
        }

        // Diálogo de confirmación de eliminación
        if (showDeleteConfirmation) {
            ConfirmDialog(
                title = "Eliminar Vehículo",
                message = "¿Está seguro que desea eliminar el vehículo con placa ${carToDelete?.placa}?",
                confirmText = "Eliminar",
                dismissText = "Cancelar",
                onConfirm = {
                    carToDelete?.let { viewModel.deleteCar(it) }
                    showDeleteConfirmation = false
                },
                onDismiss = {
                    showDeleteConfirmation = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarStockTopBar() {
    TopAppBar(
        title = { Text("Inventario de Vehículos") }
    )
}

@Composable
private fun AddCarFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Agregar Vehículo"
        )
    }
}

@Composable
private fun CarStockContent(
    modifier: Modifier = Modifier,
    cars: List<Car>,
    onEditCar: (Car) -> Unit,
    onDeleteCar: (Car) -> Unit
) {
    Column(
        modifier = modifier.padding(12.dp)
    ) {
        if (cars.isEmpty()) {
            EmptyCarList()
        } else {
            CarList(
                cars = cars,
                onEditCar = onEditCar,
                onDeleteCar = onDeleteCar
            )
        }
    }
}

@Composable
private fun EmptyCarList() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No hay vehículos registrados",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Utiliza el botón + para agregar un vehículo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CarList(
    cars: List<Car>,
    onEditCar: (Car) -> Unit,
    onDeleteCar: (Car) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cars.size) { index ->
            CarItem(
                car = cars[index],
                onEdit = { onEditCar(cars[index]) },
                onDelete = { onDeleteCar(cars[index]) }
            )
        }
    }
}

@Composable
fun CarItem(
    car: Car,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            CarDetails(car)
            Spacer(modifier = Modifier.height(8.dp))
            CarItemActions(onEdit, onDelete)
        }
    }
}

@Composable
private fun CarDetails(car: Car) {
    Column {
        CarPropertyRow("Placa", car.placa)
        CarPropertyRow("Modelo", car.modelo)
        CarPropertyRow("Chofer", car.chofer)
        CarPropertyRow("Estado", if (car.activo) "Activo" else "Inactivo")
    }
}

@Composable
private fun CarPropertyRow(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CarItemActions(onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Editar")
        }

        Button(
            onClick = onDelete,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Eliminar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCarDialog(
    car: Car?,
    onDismiss: () -> Unit,
    onSave: (Car) -> Unit
) {
    val isEditing = car != null
    var placa by remember { mutableStateOf(car?.placa ?: "") }
    var modelo by remember { mutableStateOf(car?.modelo ?: "") }
    var chofer by remember { mutableStateOf(car?.chofer ?: "") }
    var activo by remember { mutableStateOf(car?.activo ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (isEditing) "Editar Vehículo" else "Agregar Vehículo")
        },
        text = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                OutlinedTextField(
                    value = placa,
                    onValueChange = { placa = it },
                    label = { Text("Placa") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = !isEditing // No permitir editar la placa si estamos editando
                )

                OutlinedTextField(
                    value = modelo,
                    onValueChange = { modelo = it },
                    label = { Text("Modelo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = chofer,
                    onValueChange = { chofer = it },
                    label = { Text("Chofer") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Activo")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = activo,
                        onCheckedChange = { activo = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newCar = car?.copy(
                        modelo = modelo,
                        chofer = chofer,
                        activo = activo
                    ) ?: car?.let {
                        Car(
                            placa = placa,
                            modelo = modelo,
                            chofer = chofer,
                            activo = activo,
                            _id = it._id,
                            _idKit = car._idKit
                        )
                    }
                    if (newCar != null) {
                        onSave(newCar)
                    }
                },
                enabled = placa.isNotBlank() && modelo.isNotBlank() && chofer.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}