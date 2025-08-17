package com.example.icecreampos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.icecreampos.data.model.Cart
import com.example.icecreampos.data.model.Category
import com.example.icecreampos.data.model.OrderItem
import com.example.icecreampos.data.model.Product
import com.example.icecreampos.ui.navigation.Screen
import com.example.icecreampos.ui.screens.components.ToppingSelectionDialog
import com.example.icecreampos.ui.viewmodel.CartViewModel
import com.example.icecreampos.ui.viewmodel.CategoryViewModel
import com.example.icecreampos.ui.viewmodel.ProductViewModel
import com.example.icecreampos.ui.viewmodel.ToppingViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    isAdmin: Boolean,
    categoryViewModel: CategoryViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel,
    toppingViewModel: ToppingViewModel = viewModel()
) {
    val categories by categoryViewModel.categories.collectAsState()
    val products by productViewModel.products.collectAsState()
    val cart by cartViewModel.cart.collectAsState()
    val allToppings by toppingViewModel.toppings.collectAsState()

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var showToppingDialog by remember { mutableStateOf(false) }
    var productForToppingSelection by remember { mutableStateOf<Product?>(null) }

    if (showToppingDialog && productForToppingSelection != null) {
        ToppingSelectionDialog(
            product = productForToppingSelection!!,
            allToppings = allToppings,
            onDismiss = { showToppingDialog = false },
            onConfirm = { selectedToppings ->
                val newOrderItem = OrderItem(
                    product = productForToppingSelection!!,
                    quantity = 1,
                    selectedToppings = selectedToppings
                )
                cartViewModel.addOrderItem(newOrderItem)
                showToppingDialog = false
            }
        )
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Main content area
        Column(
            modifier = Modifier
                .weight(0.65f)
                .padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 16.dp)
        ) {
            PosTopBar(navController, isAdmin)
            Spacer(modifier = Modifier.height(16.dp))
            CategorySelector(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { categoryId ->
                    selectedCategoryId = categoryId
                    productViewModel.onCategorySelected(categoryId)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProductGrid(
                products = products,
                onProductSelected = { product ->
                    if (product.includedToppings > 0) {
                        productForToppingSelection = product
                        showToppingDialog = true
                    } else {
                        cartViewModel.addOrderItem(OrderItem(product = product, quantity = 1))
                    }
                }
            )
        }
        // Side cart
        Surface(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
        ) {
            SideCart(
                cart = cart,
                onUpdateQuantity = { itemId, newQuantity ->
                    cartViewModel.updateQuantity(itemId, newQuantity)
                },
                onRemoveItem = { itemId ->
                    cartViewModel.removeItem(itemId)
                }
            )
        }
    }
}

@Composable
fun PosTopBar(navController: NavController, isAdmin: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Ice Cream POS", style = MaterialTheme.typography.headlineMedium)
        if (isAdmin) {
            Button(onClick = { navController.navigate(Screen.Admin.route) }) {
                Text(text = "Admin Panel")
            }
        }
    }
}

@Composable
fun CategorySelector(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories.filter { it.visible }) { category ->
            val isSelected = category.id == selectedCategoryId
            Button(
                onClick = { onCategorySelected(category.id) },
                colors = if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder
            ) {
                Text(category.name)
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
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier.padding(top = 8.dp, end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            Card(
                modifier = Modifier.clickable { onProductSelected(product) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.height(120.dp).fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "$${"%.2f".format(product.price)}", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary))
                    }
                }
            }
        }
    }
}

@Composable
fun SideCart(
    cart: Cart,
    onUpdateQuantity: (orderItemId: String, newQuantity: Int) -> Unit,
    onRemoveItem: (orderItemId: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxHeight().padding(16.dp)) {
        Text(text = "Current Order", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        LazyColumn(modifier = Modifier.weight(1f)) {
            if (cart.items.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Cart is empty", style = MaterialTheme.typography.bodyMedium)
                    }
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
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal:", style = MaterialTheme.typography.bodyLarge)
                Text("$${"%.2f".format(cart.subtotal)}", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("$${"%.2f".format(cart.total)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (cart.items.isNotEmpty()) {
                        navController.navigate(Screen.Payment.createRoute(cart.total.toFloat()))
                    }
                },
                enabled = cart.items.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("PAY", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: OrderItem,
    onUpdateQuantity: (orderItemId: String, newQuantity: Int) -> Unit,
    onRemoveItem: (orderItemId: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.product.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            if (item.selectedToppings.isNotEmpty()) {
                Text(
                    text = "  + " + item.selectedToppings.joinToString { it.name },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(text = "$${"%.2f".format(item.totalPrice)}", style = MaterialTheme.typography.bodyMedium)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
                onClick = { onUpdateQuantity(item.id, item.quantity - 1) },
                modifier = Modifier.size(28.dp).clip(CircleShape)
            ) {
                Icon(Icons.Default.RemoveCircle, contentDescription = "Decrease quantity", tint = MaterialTheme.colorScheme.secondary)
            }
            Text(
                text = item.quantity.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(
                onClick = { onUpdateQuantity(item.id, item.quantity + 1) },
                modifier = Modifier.size(28.dp).clip(CircleShape)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = "Increase quantity", tint = MaterialTheme.colorScheme.secondary)
            }
            IconButton(
                onClick = { onRemoveItem(item.id) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove item", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
