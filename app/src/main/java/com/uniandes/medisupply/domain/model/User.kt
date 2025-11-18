package com.uniandes.medisupply.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: UserRole
) : Parcelable

enum class UserRole(val displayName: String) {
    CLIENT("cliente"),
    VENDOR("vendedor");

    companion object {
        fun fromDisplayName(displayName: String): UserRole? {
            return entries.find { it.displayName.equals(displayName, ignoreCase = true) }
        }
    }
}
