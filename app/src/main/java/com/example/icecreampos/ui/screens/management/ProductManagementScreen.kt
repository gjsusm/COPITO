package com.example.icecreampos.ui.screens.management

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.icecreampos.data.model.Product
import com.example.icecreampos.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel()
) {
    val categories by productViewModel.categories.collectAsState()
    val products by productViewModel.products.collectAsState()
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Products") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                productToEdit = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = categories.find { it.id == selectedCategory }?.name ?: "Select a Category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category.id
                                productViewModel.onCategorySelected(category.id)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onEditClick = {
                            productToEdit = product
                            showDialog = true
                        },
                        onDeleteClick = { productViewModel.deleteProduct(product.id) }
                    )
                }
            }
        }

        if (showDialog && selectedCategory != null) {
            ProductDialog(
                product = productToEdit,
                onDismiss = { showDialog = false },
                onConfirm = { name, price, description, imageUri ->
                    val productData = productToEdit?.copy(
                        name = name,
                        price = price,
                        description = description
                    ) ?: Product(
                        name = name,
                        price = price,
                        description = description,
                        categoryId = selectedCategory!!
                    )

                    if (productToEdit == null) {
                        productViewModel.addProduct(productData, imageUri)
                    } else {
                        productViewModel.updateProduct(productData, imageUri)
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (product.imageUrl.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(product.imageUrl),
                    contentDescription = product.name,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(text = "${product.name} - $${"%.2f".format(product.price)}", modifier = Modifier.weight(1f))
            IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, "Edit") }
            IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, "Delete") }
        }
    }
}

@Composable
fun ProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, price: Double, description: String, imageUri: Uri?) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Add Product" else "Edit Product") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Selected product image",
                        modifier = Modifier.size(100.dp)
                    )
                } else if (product?.imageUrl?.isNotBlank() == true) {
                    Image(
                        painter = rememberAsyncImagePainter(product.imageUrl),
                        contentDescription = "Current product image",
                        modifier = Modifier.size(100.dp)
                    )
                }
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Select Image")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(name, price.toDoubleOrNull() ?: 0.0, description, imageUri)
            }) { Text("Confirm") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
