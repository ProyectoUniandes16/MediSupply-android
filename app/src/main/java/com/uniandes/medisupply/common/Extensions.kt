package com.uniandes.medisupply.common

import android.content.Intent
import android.os.Build
import android.os.Parcelable
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

// kotlin
fun Double.formatCurrency(
    currencyCode: String? = "USD",
    perUnits: Int = 1,
    usdToCop: Double = 3802.50,
    usdToMxn: Double = 18.45
): String {
    val locale = Locale.getDefault()
    val (targetCode, rate) = when {
        locale.language == "es" && locale.country.equals("MX", ignoreCase = true) -> "MXN" to usdToMxn
        locale.language == "es" && locale.country.equals("CO", ignoreCase = true) -> "COP" to usdToCop
        else -> (currencyCode ?: "USD") to 1.0
    }

    val converted = perUnits * rate * this

    val nf = NumberFormat.getCurrencyInstance(locale)
    try { nf.currency = Currency.getInstance(targetCode) } catch (_: Exception) {}

    val formatted = nf.format(converted)
    return "$formatted $targetCode"
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ExcludeFromJacocoGeneratedReport

inline fun <reified T : Parcelable> Intent.getParcelableExtraProvider(key: String): T? {
    // The 'inline' and 'reified' keywords allow you to get the class type T at runtime.
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(key) as? T
    }
}
