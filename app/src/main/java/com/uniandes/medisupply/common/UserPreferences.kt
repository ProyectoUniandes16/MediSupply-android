package com.uniandes.medisupply.common

import android.content.Context
import android.content.SharedPreferences

class UserPreferences private constructor(private val prefs: SharedPreferences) {

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val BASE_URL = "base_url"
        private const val ROLE = "role"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val PHONE = "phone"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun init(context: Context) {
            getInstance(context)
        }

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(
                    context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                ).also { INSTANCE = it }
            }
        }

        // Only for test unitarios
        internal fun clearInstanceForTests() {
            INSTANCE = null
        }
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun setAccessToken(token: String?) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(IS_LOGGED_IN, false)
    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean(IS_LOGGED_IN, loggedIn).apply()
    }

    fun getBaseUrl(): String? = prefs.getString(BASE_URL, null)
    fun setBaseUrl(baseUrl: String) {
        prefs.edit().putString(BASE_URL, baseUrl).apply()
    }
    fun setRole(role: String) {
        prefs.edit().putString(ROLE, role).apply()
    }
    fun getRole(): String? = prefs.getString(ROLE, null)
    fun setName(name: String) {
        prefs.edit().putString(NAME, name).apply()
    }
    fun getName(): String? = prefs.getString(NAME, null)
    fun setEmail(email: String) {
        prefs.edit().putString(EMAIL, email).apply()
    }
    fun getEmail(): String? = prefs.getString(EMAIL, null)
    fun setPhone(phone: String) {
        prefs.edit().putString(PHONE, phone).apply()
    }
    fun getPhone(): String? = prefs.getString(PHONE, null)
}

fun Context.userPreferences(): UserPreferences = UserPreferences.getInstance(this)
