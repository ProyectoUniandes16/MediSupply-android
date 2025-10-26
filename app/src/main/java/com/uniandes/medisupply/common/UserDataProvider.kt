package com.uniandes.medisupply.common

interface UserDataProvider {
    fun getAccessToken(): String
    fun setAccessToken(accessToken: String)
    fun setUserLoggedIn(loggedIn: Boolean)
    fun isUserLoggedIn(): Boolean
}

class UserDataProviderImpl(
    private val userPreferences: UserPreferences
) : UserDataProvider {
    private var accessToken: String = ""
    override fun getAccessToken(): String {
        if (accessToken.isEmpty()) {
            accessToken = userPreferences.getAccessToken() ?: ""
        }
        return accessToken
    }

    override fun setAccessToken(accessToken: String) {
        userPreferences.setAccessToken(accessToken)
        this.accessToken = accessToken
    }

    override fun setUserLoggedIn(loggedIn: Boolean) {
        userPreferences.setLoggedIn(loggedIn)
    }

    override fun isUserLoggedIn(): Boolean {
        return userPreferences.isLoggedIn()
    }
}