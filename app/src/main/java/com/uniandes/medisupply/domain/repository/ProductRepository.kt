package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.common.resultOrError
import com.uniandes.medisupply.data.remote.service.ProductService
import com.uniandes.medisupply.domain.model.Product
import com.uniandes.medisupply.domain.model.toDomain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductById(id: Int): Result<Product>
    suspend fun uploadProductVideo(
        id: Int,
        fileName: String,
        fileBytes: ByteArray,
        mediaType: String?,
        description: String
    ): Result<Unit>
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

    override suspend fun uploadProductVideo(
        id: Int,
        fileName: String,
        fileBytes: ByteArray,
        mediaType: String?,
        description: String
    ): Result<Unit> {
        return resultOrError {
            val requestFile = fileBytes.toRequestBody(
                contentType = mediaType?.toMediaTypeOrNull()
            )
            val videoPart = MultipartBody.Part.createFormData(
                "video",
                fileName,
                requestFile
            )
            productService.uploadVideo(id, videoPart, description)
        }
    }
}
