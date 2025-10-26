package com.uniandes.medisupply.common

fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    return this.isNotBlank() && Regex(emailRegex).matches(this)
}

fun String.isValidPhone(): Boolean {
    val phoneRegex = "^[+]?[0-9]{7,15}\$"
    return this.isNotBlank() && Regex(phoneRegex).matches(this)
}
