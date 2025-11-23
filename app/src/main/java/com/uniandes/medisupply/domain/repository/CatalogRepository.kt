package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.common.resultOrError
import com.uniandes.medisupply.data.remote.service.CatalogService

interface CatalogRepository {
    suspend fun getZones(): Result<List<String>>
}

class CatalogRepositoryImpl(
    private val catalogService: CatalogService
) : CatalogRepository {
    override suspend fun getZones(): Result<List<String>> {
        return resultOrError {
            val response = catalogService.getZones()
            response.data.map { it.name }
        }
    }
}