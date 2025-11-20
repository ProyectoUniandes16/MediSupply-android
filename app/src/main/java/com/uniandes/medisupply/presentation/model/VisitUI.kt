package com.uniandes.medisupply.presentation.model

import androidx.annotation.StringRes
import com.uniandes.medisupply.R
import com.uniandes.medisupply.domain.model.Visit

data class VisitUI(
    val status: VisitStatusUI,
    val visitDate: String,
    val clientName: String,
    val contactName: String,
    val clientAddress: String
)

enum class VisitStatusUI(@StringRes val resId: Int) {
    COMPLETED(R.string.pending),
    PENDING(R.string.pending),
    IN_PROGRESS(R.string.in_progress)
}

fun Visit.toUi(): VisitUI {
    return VisitUI(
        status = when (this.status) {
            "finalizado" -> VisitStatusUI.COMPLETED
            "pendiente" -> VisitStatusUI.PENDING
            else -> VisitStatusUI.IN_PROGRESS
        },
        visitDate = this.visitDate,
        clientName = this.client.name,
        clientAddress = this.client.address,
        contactName = this.client.contactInfo.name
    )
}
