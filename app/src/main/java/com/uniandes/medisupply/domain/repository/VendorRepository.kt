package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.common.resultOrError
import com.uniandes.medisupply.data.remote.model.visit.UpdateVisitStatusRequest
import com.uniandes.medisupply.data.remote.service.VendorService
import com.uniandes.medisupply.domain.model.Visit
import com.uniandes.medisupply.domain.model.VisitStatus
import com.uniandes.medisupply.domain.model.toDomain

interface VendorRepository {
    suspend fun getVisits(startDate: String, endDate: String): Result<List<Visit>>
    suspend fun updateVisitStatus(visitId: Int, status: VisitStatus): Result<Unit>
}

class VendorRepositoryImpl(
    private val vendorService: VendorService
) : VendorRepository {
    override suspend fun getVisits(startDate: String, endDate: String): Result<List<Visit>> {
        return resultOrError {
            vendorService.getVisits(startDate, endDate).data.map { visitResponse ->
                Visit(
                    status = VisitStatus.fromRawValue(visitResponse.status)!!,
                    visitDate = visitResponse.visitDate,
                    client = visitResponse.client.toDomain(),
                    id = visitResponse.id
                )
            }
        }
    }

    override suspend fun updateVisitStatus(visitId: Int, status: VisitStatus): Result<Unit> {
        return resultOrError {
            vendorService.updateVisitStatus(
                visitId,
                UpdateVisitStatusRequest(status.rawValue)
            )
            Unit
        }
    }
}
