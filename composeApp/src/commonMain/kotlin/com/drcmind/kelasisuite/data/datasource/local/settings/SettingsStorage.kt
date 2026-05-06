package com.drcmind.kelasisuite.data.datasource.local.settings

import kotlinx.serialization.Serializable

interface SettingsStorage {
    fun saveUserInfo(token: String, username: String, role: String)
    fun getUserInfo(): UserInfo?
    fun getToken(): String?
    fun clearUserInfo()
}


@Serializable
data class UserInfo(
    val username: String, val roles: String
)