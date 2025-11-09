package com.uniandes.medisupply.repository

import com.uniandes.medisupply.data.remote.model.common.DataResponse
import com.uniandes.medisupply.data.remote.model.product.ProductResponse
import com.uniandes.medisupply.data.remote.service.ProductService
import com.uniandes.medisupply.domain.repository.ProductRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductRepositoryTest {
    private val productService: ProductService = mockk()
    private val productRepository = ProductRepositoryImpl(productService)

    @Test
    fun `getProducts SHOULD return a list of products WHEN service returns success`() = runTest {
        // GIVEN
        val productList = PRODUCT_LIST
        coEvery { productService.getProducts() } returns DataResponse(
            productList
        )

        // WHEN
        val result = productRepository.getProducts()

        // THEN
        assertTrue(result.isSuccess)
        assertEquals(productList.size, result.getOrNull()?.size)
    }

    @Test
    fun `getProducts SHOULD throws error  WHEN service fails`() = runTest {
        // GIVEN
        val productList = PRODUCT_LIST
        val exceptionMessage = "Network Error"
        coEvery { productService.getProducts() } throws Exception(exceptionMessage)

        // WHEN
        val result = productRepository.getProducts()

        // THEN
        assertTrue(result.isFailure)
        assertEquals(exceptionMessage, result.exceptionOrNull()?.message)
    }

    companion object {
        val PRODUCT = ProductResponse(
            id = 1,
            name = "Product 1",
            unitPrice = 10.0,
            availableStock = 100,
            storageConditions = "Room Temperature",
            expirationDate = "2025-12-31",
            sku = "SKU12345",
            status = "Available",
            category = "Medicine"
        )
        val PRODUCT_LIST = List(10) { PRODUCT.copy(id = it + 1, name = "Product ${it + 1}") }
    }
}
