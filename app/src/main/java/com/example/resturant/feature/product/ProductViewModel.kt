package com.example.resturant.feature.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.resturant.feature.product.data.Product
import com.example.resturant.feature.product.data.ProductDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ProductDatabase.getInstance(application).productDao()

    val products: Flow<List<Product>> = dao.getAll()

    fun save(product: Product) = viewModelScope.launch {
        if (product.id == 0L) dao.insert(product) else dao.update(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        dao.delete(product)
    }
}