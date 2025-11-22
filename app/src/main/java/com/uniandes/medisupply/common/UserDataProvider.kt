package com.uniandes.medisupply.common

import com.uniandes.medisupply.domain.model.User

interface UserDataProvider {
    fun getAccessToken(): String
    fun setUserData(accessToken: String, user: User)
    fun setBaseUrl(baseUrl: String)
    fun getRole(): String
    fun isUserLoggedIn(): Boolean
    fun clearAll()
    fun getName(): String
    fun getEmail(): String
    fun getPhone(): String
    fun clearUserData()
}

class UserDataProviderImpl(
    private val userPreferences: UserPreferences
) : UserDataProvider {
    private var accessToken: String = ""
    private var role: String = ""
    override fun getAccessToken(): String {
        if (accessToken.isEmpty()) {
            accessToken = userPreferences.getAccessToken() ?: ""
        }
        return accessToken
    }

    override fun setUserData(accessToken: String, user: User) {
        this.accessToken = accessToken
        userPreferences.setAccessToken(accessToken)
        userPreferences.setLoggedIn(true)
        userPreferences.setRole(user.role.displayName)
        userPreferences.setName(user.name)
        userPreferences.setEmail(user.email)
        userPreferences.setPhone(user.email)
    }

    override fun setBaseUrl(baseUrl: String) {
        userPreferences.setBaseUrl(baseUrl)
    }

    override fun getRole(): String {
        if (role.isEmpty()) {
            role = userPreferences.getRole() ?: ""
        }
        return role
    }

    override fun isUserLoggedIn(): Boolean {
        return userPreferences.isLoggedIn()
    }

    override fun clearAll() {
        return userPreferences.clearAll()
    }

    override fun getName(): String {
        return userPreferences.getName() ?: ""
    }

    override fun getEmail(): String {
        return userPreferences.getEmail() ?: ""
    }

    override fun getPhone(): String {
        return userPreferences.getPhone() ?: ""
    }

    override fun clearUserData() {
        val url = userPreferences.getBaseUrl() ?: ""
        userPreferences.clearAll()
        userPreferences.setBaseUrl(url)
    }
}
