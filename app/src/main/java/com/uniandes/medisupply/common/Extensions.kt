package com.uniandes.medisupply.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    return this.isNotBlank() && Regex(emailRegex).matches(this)
}

fun String.isValidPhone(): Boolean {
    val phoneRegex = "^[+]?[0-9]{7,15}\$"
    return this.isNotBlank() && Regex(phoneRegex).matches(this)
}
@Serializable
data class ErrorResponse(
    @SerialName("codigo")
    val code: String? = null,
    val error: String? = null
)

inline fun <T> resultOrError(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        val mappedException = when (e) {
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = errorBody?.let {
                    runCatching { Json.decodeFromString<ErrorResponse>(it) }.getOrNull()
                }
                errorResponse?.let { NetworkException(code = it.code, message = it.error) } ?: e
            }
            else -> {
                e
            }
        }
        Result.failure(mappedException)
    }
}

fun Double.formatCurrency(currencyCode: String? = "USD"): String {
    val locale = Locale.getDefault()
    val nf = NumberFormat.getCurrencyInstance(locale)
    if (currencyCode != null) {
        try { nf.currency = Currency.getInstance(currencyCode) } catch (_: Exception) {}
    }
    return nf.format(this)
}
