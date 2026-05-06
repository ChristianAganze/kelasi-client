package com.drcmind.kelasisuite.data.datasource.local.settings

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsKeys.KEY_ROLE
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsKeys.KEY_TOKEN
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsKeys.KEY_USERNAME
import com.russhwolf.settings.Settings

class SettingsStorageImpl(private val settings: Settings) : SettingsStorage {
    override fun saveUserInfo(token: String, username: String, role : String) {
        println("Saving userinfo to settings...")
        settings.putString(KEY_TOKEN, token)
        settings.putString(KEY_USERNAME, username)
        settings.putString(KEY_ROLE, role)
    }


    override fun getUserInfo(): UserInfo {
        val storedUsername =
            settings.getString(KEY_USERNAME, "Utilisateur") // "username" est la valeur par défaut
        val storedRole = settings.getString(KEY_ROLE, "Role")
        return UserInfo(storedUsername, storedRole)
    }


    override fun getToken(): String? {
        val token = settings.getStringOrNull(KEY_TOKEN)
        println("Reading token from settings: ${if (token != null) "Found" else "Not Found"}")
        return token
    }

    override fun clearUserInfo() {
        println("Clearing all tokens from settings...")
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USERNAME)
        settings.remove(KEY_ROLE)
    }

}