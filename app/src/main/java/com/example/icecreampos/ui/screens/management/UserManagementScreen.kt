package com.example.icecreampos.ui.screens.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icecreampos.data.model.User
import com.example.icecreampos.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val users by userViewModel.users.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var userToEdit by remember { mutableStateOf<User?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Users") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                userToEdit = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
                UserItem(
                    user = user,
                    onEditClick = {
                        userToEdit = user
                        showDialog = true
                    },
                    onDeleteClick = { userViewModel.deleteUser(user.uid) }
                )
            }
        }

        if (showDialog) {
            UserDialog(
                user = userToEdit,
                onDismiss = { showDialog = false },
                onConfirmCreate = { name, email, password, role ->
                    userViewModel.createUser(name, email, password, role)
                    showDialog = false
                },
                onConfirmUpdate = { user ->
                    userViewModel.updateUser(user)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun UserItem(
    user: User,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name, fontWeight = FontWeight.Bold)
                Text(text = user.email)
                Text(text = "Role: ${user.role}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, "Edit") }
            IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, "Delete") }
        }
    }
}

@Composable
fun UserDialog(
    user: User?,
    onDismiss: () -> Unit,
    onConfirmCreate: (name: String, email: String, password: String, role: String) -> Unit,
    onConfirmUpdate: (user: User) -> Unit
) {
    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(user?.role ?: "employee") }
    var active by remember { mutableStateOf(user?.active ?: true) }

    val isEditMode = user != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditMode) "Edit User" else "Add User") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    enabled = !isEditMode // Can't edit email
                )
                if (!isEditMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Simple dropdown for role
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("employee") }, onClick = { role = "employee"; expanded = false })
                        DropdownMenuItem(text = { Text("admin") }, onClick = { role = "admin"; expanded = false })
                    }
                }
                if (isEditMode) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Active")
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = active, onCheckedChange = { active = it })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (isEditMode) {
                    onConfirmUpdate(user!!.copy(name = name, role = role, active = active))
                } else {
                    onConfirmCreate(name, email, password, role)
                }
            }) { Text("Confirm") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
