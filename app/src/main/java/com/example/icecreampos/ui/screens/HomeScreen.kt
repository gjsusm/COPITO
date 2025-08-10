package com.example.icecreampos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.icecreampos.data.model.Cart
import com.example.icecreampos.data.model.Category
import com.example.icecreampos.data.model.OrderItem
import com.example.icecreampos.data.model.Product
import com.example.icecreampos.ui.navigation.Screen
import com.example.icecreampos.ui.viewmodel.CartViewModel
import com.example.icecreampos.ui.viewmodel.CategoryViewModel
import com.example.icecreampos.ui.viewmodel.ProductViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    isAdmin: Boolean,
    categoryViewModel: CategoryViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val categories by categoryViewModel.categories.collectAsState()
    val products by productViewModel.products.collectAsState()
    val cart by cartViewModel.cart.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        // Main content area (70%)
        Column(modifier = Modifier.weight(0.7f)) {
            PosTopBar(navController, isAdmin)
            CategorySelector(
                categories = categories,
                onCategorySelected = { categoryId ->
                    productViewModel.onCategorySelected(categoryId)
                }
            )
            ProductGrid(
                products = products,
                onProductSelected = { product ->
                    cartViewModel.addProductToCart(product)
                }
            )
        }
        // Side cart (30%)
        Column(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            SideCart(
                cart = cart,
                onUpdateQuantity = { productId, newQuantity ->
                    cartViewModel.updateQuantity(productId, newQuantity)
                },
                onRemoveItem = { productId ->
                    cartViewModel.removeProductFromCart(productId)
                }
            )
        }
    }
}

@Composable
fun PosTopBar(navController: NavController, isAdmin: Boolean) {
    Surface(shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Ice Cream POS", style = MaterialTheme.typography.titleLarge)
            if (isAdmin) {
                Button(onClick = { navController.navigate(Screen.Admin.route) }) {
                    Text(text = "Admin Panel")
                }
            }
        }
    }
}

@Composable
fun CategorySelector(
    categories: List<Category>,
    onCategorySelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Categories", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories.filter { it.visible }) { category ->
                Button(onClick = { onCategorySelected(category.id) }) {
                    Text(category.name)
                }
            }
        }
    }
}

@Composable
fun ProductGrid(
    products: List<Product>,
    onProductSelected: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            Card(
                modifier = Modifier
                    .height(180.dp)
                    .clickable { onProductSelected(product) }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = product.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(text = "$${"%.2f".format(product.price)}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun SideCart(
    cart: Cart,
    onUpdateQuantity: (productId: String, newQuantity: Int) -> Unit,
    onRemoveItem: (productId: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxHeight()) {
        Text(text = "Current Order", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        // Cart items list
        LazyColumn(modifier = Modifier.weight(1f)) {
            if (cart.items.isEmpty()) {
                item {
                    Text("Cart is empty", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(cart.items) { item ->
                    CartItemRow(
                        item = item,
                        onUpdateQuantity = onUpdateQuantity,
                        onRemoveItem = onRemoveItem
                    )
                    Divider()
                }
            }
        }
        // Totals and buttons
        Column {
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal:", style = MaterialTheme.typography.bodyLarge)
                Text("$${"%.2f".format(cart.subtotal)}", style = MaterialTheme.typography.bodyLarge)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tax:", style = MaterialTheme.typography.bodyLarge)
                Text("$${"%.2f".format(cart.tax)}", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$${"%.2f".format(cart.total)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /*TODO: Navigate to Payment*/ }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("PAY", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: OrderItem,
    onUpdateQuantity: (productId: String, newQuantity: Int) -> Unit,
    onRemoveItem: (productId: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.product.name, fontWeight = FontWeight.Bold)
            Text(text = "$${"%.2f".format(item.totalPrice)}")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onUpdateQuantity(item.product.id, item.quantity - 1) }) {
                Icon(Icons.Default.RemoveCircle, contentDescription = "Decrease quantity")
            }
            Text(text = item.quantity.toString(), style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = { onUpdateQuantity(item.product.id, item.quantity + 1) }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Increase quantity")
            }
            IconButton(onClick = { onRemoveItem(item.product.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove item")
            }
        }
    }
}
