package com.drcmind.kelasisuite.data.datasource.settings

interface SettingsStorage {
    fun saveUserInfo(token: String, username: String, role : String)
    fun getToken(): String?
    fun clearUserInfo()
}