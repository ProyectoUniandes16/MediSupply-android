package com.uniandes.medisupply.common

import android.content.Context
import android.content.SharedPreferences

class UserPreferences private constructor(private val prefs: SharedPreferences) {

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val IS_LOGGED_IN = "is_logged_in"

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
}

fun Context.userPreferences(): UserPreferences = UserPreferences.getInstance(this)
