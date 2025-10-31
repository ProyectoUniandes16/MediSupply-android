package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.common.resultOrError
import com.uniandes.medisupply.data.remote.service.ProductService
import com.uniandes.medisupply.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
}

class ProductRepositoryImpl(private val productService: ProductService) : ProductRepository {
    override suspend fun getProducts(): Result<List<Product>> {
        val response = productService.getProducts()
        return resultOrError {
            response.data.products.map {
                Product(
                    id = it.id,
                    name = it.name,
                    price = it.unitPrice,
                    stock = it.availableStock
                )
            }
        }
    }
}