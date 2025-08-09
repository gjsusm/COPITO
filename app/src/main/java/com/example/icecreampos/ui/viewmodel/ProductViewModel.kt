package com.example.icecreampos.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icecreampos.data.model.Category
import com.example.icecreampos.data.model.Product
import com.example.icecreampos.data.repository.CategoryRepository
import com.example.icecreampos.data.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Using the fully qualified name for the annotation to avoid ambiguity.
@kotlinx.coroutines.ExperimentalCoroutinesApi
class ProductViewModel : ViewModel() {

    private val productRepository = ProductRepository()
    private val categoryRepository = CategoryRepository()

    val categories: StateFlow<List<Category>> = categoryRepository.getCategoriesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCategoryId = MutableStateFlow<String?>(null)

    val products: StateFlow<List<Product>> = _selectedCategoryId.flatMapLatest { categoryId ->
        if (categoryId == null) {
            flowOf(emptyList())
        } else {
            productRepository.getProductsStream(categoryId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onCategorySelected(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    fun addProduct(product: Product, imageUri: Uri?) {
        viewModelScope.launch {
            val productId = productRepository.addProduct(product)
            if (imageUri != null) {
                val imageUrl = productRepository.uploadProductImage(productId, imageUri)
                val updatedProduct = product.copy(id = productId, imageUrl = imageUrl)
                productRepository.updateProduct(updatedProduct)
            }
        }
    }

    fun updateProduct(product: Product, imageUri: Uri?) {
        viewModelScope.launch {
            val productToUpdate = if (imageUri != null) {
                val imageUrl = productRepository.uploadProductImage(product.id, imageUri)
                product.copy(imageUrl = imageUrl)
            } else {
                product
            }
            productRepository.updateProduct(productToUpdate)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
        }
    }
}
