package com.drcmind.kelasisuite.data.datasource.local.settings

import com.drcmind.kelasisuite.domain.model.UserInfo
import kotlinx.serialization.Serializable
import kotlin.time.Instant

interface SettingsStorage {
    fun saveUserInfo(token: String, username: String, role: String)
    fun getUserInfo(): UserInfo
    fun getToken(): String?
    fun clearUserInfo()
    fun isTokenExpired(): Boolean
    fun getTokenExpirationDate(): Instant?
}