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
import com.example.icecreampos.data.model.Category
import com.example.icecreampos.ui.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    navController: NavController,
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val categories by categoryViewModel.categories.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                categoryToEdit = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    onEditClick = {
                        categoryToEdit = category
                        showDialog = true
                    },
                    onDeleteClick = { categoryViewModel.deleteCategory(category.id) }
                )
            }
        }

        if (showDialog) {
            CategoryDialog(
                category = categoryToEdit,
                onDismiss = { showDialog = false },
                onConfirm = { name, order, visible ->
                    if (categoryToEdit == null) {
                        categoryViewModel.addCategory(name, order, visible)
                    } else {
                        categoryViewModel.updateCategory(
                            categoryToEdit!!.copy(
                                name = name,
                                order = order,
                                visible = visible
                            )
                        )
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
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
            Text(text = "${category.name} (Order: ${category.order})", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun CategoryDialog(
    category: Category?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, order: Int, visible: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var order by remember { mutableStateOf(category?.order?.toString() ?: "0") }
    var visible by remember { mutableStateOf(category?.visible ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Add Category" else "Edit Category") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = order,
                    onValueChange = { order = it.filter { char -> char.isDigit() } },
                    label = { Text("Order") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Visible")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(checked = visible, onCheckedChange = { visible = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val orderInt = order.toIntOrNull() ?: 0
                onConfirm(name, orderInt, visible)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
