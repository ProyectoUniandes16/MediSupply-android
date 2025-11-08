package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.common.resultOrError
import com.uniandes.medisupply.data.remote.service.ProductService
import com.uniandes.medisupply.domain.model.Product
import com.uniandes.medisupply.domain.model.StockStatus
import com.uniandes.medisupply.domain.model.toDomain

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductById(id: Int): Result<Product>
}

class ProductRepositoryImpl(private val productService: ProductService) : ProductRepository {
    override suspend fun getProducts(): Result<List<Product>> {
        return resultOrError {
            val response = productService.getProducts()
            response.data.map {
                it.toDomain()
            }
        }
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        return resultOrError {
            val response = productService.getProductById(id)
            val data = response.data
            data.toDomain()
        }
    }
}
