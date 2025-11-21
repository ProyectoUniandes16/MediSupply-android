package com.uniandes.medisupply.domain.model

data class Visit(
    val id: Int,
    val status: VisitStatus,
    val visitDate: String,
    val client: Client
)

enum class VisitStatus(val rawValue: String) {
    PENDING("pendiente"),
    COMPLETED("finalizado"),
    IN_PROGRESS("en progreso");

    companion object {
        fun fromRawValue(displayName: String): VisitStatus? {
            return entries.find { it.rawValue == displayName }
        }
    }
}
