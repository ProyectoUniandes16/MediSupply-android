package com.uniandes.medisupply.data.remote.service

import com.uniandes.medisupply.data.remote.model.common.DataResponse
import com.uniandes.medisupply.data.remote.model.visit.VisitResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface VendorService {
    @GET("/movil/visitas")
    suspend fun getVisits(
        @Query("fecha_inicio")
        startDate: String,
        @Query("fecha_fin")
        endDate: String
    ): DataResponse<List<VisitResponse>>
}