package com.example.icecreampos.ui.screens.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icecreampos.data.model.Topping
import com.example.icecreampos.ui.viewmodel.ToppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToppingManagementScreen(
    navController: NavController,
    toppingViewModel: ToppingViewModel = viewModel()
) {
    val toppings by toppingViewModel.toppings.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var toppingToEdit by remember { mutableStateOf<Topping?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Toppings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                toppingToEdit = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Topping")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(toppings) { topping ->
                ToppingItem(
                    topping = topping,
                    onEditClick = {
                        toppingToEdit = topping
                        showDialog = true
                    },
                    onDeleteClick = { toppingViewModel.deleteTopping(topping.id) }
                )
            }
        }

        if (showDialog) {
            ToppingDialog(
                topping = toppingToEdit,
                onDismiss = { showDialog = false },
                onConfirm = { name ->
                    if (toppingToEdit == null) {
                        toppingViewModel.addTopping(name)
                    } else {
                        toppingViewModel.updateTopping(
                            toppingToEdit!!.copy(name = name)
                        )
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun ToppingItem(
    topping: Topping,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = topping.name, modifier = Modifier.weight(1f))
            IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, "Edit") }
            IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, "Delete") }
        }
    }
}

@Composable
fun ToppingDialog(
    topping: Topping?,
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit
) {
    var name by remember { mutableStateOf(topping?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (topping == null) "Add Topping" else "Edit Topping") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Topping Name") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(name)
            }) { Text("Confirm") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
