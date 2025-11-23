package com.uniandes.medisupply.data.remote.service

import com.uniandes.medisupply.data.remote.model.catalog.ZoneResponse
import com.uniandes.medisupply.data.remote.model.common.DataResponse
import retrofit2.http.GET

interface CatalogService {
    @GET("/movil/zona")
    suspend fun getZones(): DataResponse<List<ZoneResponse>>
}
