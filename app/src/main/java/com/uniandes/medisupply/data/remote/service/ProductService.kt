package com.uniandes.medisupply.data.remote.service

import com.uniandes.medisupply.data.remote.model.common.DataResponse
import com.uniandes.medisupply.data.remote.model.product.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    @GET("/movil-producto/producto")
    suspend fun getProducts(@Query("per_page") size: Int = 500): DataResponse<List<ProductResponse>>
    @GET("/movil-producto/producto/{id}")
    suspend fun getProductById(@Path("id") id: Int): DataResponse<ProductResponse>
}
