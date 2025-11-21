package com.uniandes.medisupply.presentation.model

import androidx.annotation.StringRes
import com.uniandes.medisupply.R
import com.uniandes.medisupply.domain.model.Visit
import com.uniandes.medisupply.domain.model.VisitStatus

data class VisitUI(
    val id: Int,
    val status: VisitStatusUI,
    val visitDate: String,
    val clientName: String,
    val contactName: String,
    val clientAddress: String,
    val canBeStarted: Boolean = status == VisitStatusUI.PENDING
)

enum class VisitStatusUI(@StringRes val resId: Int) {
    COMPLETED(R.string.pending),
    PENDING(R.string.pending),
    IN_PROGRESS(R.string.in_progress)
}

fun Visit.toUi(): VisitUI {
    return VisitUI(
        status = this.status.toUi(),
        visitDate = this.visitDate,
        clientName = this.client.name,
        clientAddress = this.client.address,
        contactName = this.client.contactInfo.name,
        id = this.id
    )
}

fun VisitStatus.toUi() = when (this) {
    VisitStatus.COMPLETED -> VisitStatusUI.COMPLETED
    VisitStatus.PENDING -> VisitStatusUI.PENDING
    VisitStatus.IN_PROGRESS -> VisitStatusUI.IN_PROGRESS
}

fun VisitStatusUI.toDomain(): VisitStatus {
    return when (this) {
        VisitStatusUI.COMPLETED -> VisitStatus.COMPLETED
        VisitStatusUI.PENDING -> VisitStatus.PENDING
        VisitStatusUI.IN_PROGRESS -> VisitStatus.IN_PROGRESS
    }
}
