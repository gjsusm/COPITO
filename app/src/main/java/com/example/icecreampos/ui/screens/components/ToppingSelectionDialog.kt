package com.example.icecreampos.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.icecreampos.data.model.Product
import com.example.icecreampos.data.model.Topping

@Composable
fun ToppingSelectionDialog(
    product: Product,
    allToppings: List<Topping>,
    onDismiss: () -> Unit,
    onConfirm: (List<Topping>) -> Unit
) {
    var selectedToppings by remember { mutableStateOf<Set<Topping>>(emptySet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Toppings for ${product.name}") },
        text = {
            Column {
                Text("Select up to ${product.includedToppings} toppings:")
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(allToppings.filter { it.active }) { topping ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val newSelection = selectedToppings.toMutableSet()
                                    if (topping in newSelection) {
                                        newSelection.remove(topping)
                                    } else if (newSelection.size < product.includedToppings) {
                                        newSelection.add(topping)
                                    }
                                    selectedToppings = newSelection
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = topping in selectedToppings,
                                onCheckedChange = { isChecked ->
                                    val newSelection = selectedToppings.toMutableSet()
                                    if (isChecked) {
                                        if (newSelection.size < product.includedToppings) {
                                            newSelection.add(topping)
                                        }
                                    } else {
                                        newSelection.remove(topping)
                                    }
                                    selectedToppings = newSelection
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(topping.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedToppings.toList()) }) {
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
