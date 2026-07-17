package com.drcmind.kelasisuite.data.datasource.local.settings

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsKeys.KEY_ACTIVE_ACADEMIC_YEAR
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsKeys.KEY_ROLE
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsKeys.KEY_SCHOOL_DATA
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsKeys.KEY_SCHOOL_ID
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsKeys.KEY_TOKEN
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsKeys.KEY_USERNAME
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsKeys.KEY_USER_ID
import com.drcmind.kelasisuite.data.datasource.remote.dto.AcademicYearDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.model.JwtPayload
import com.drcmind.kelasisuite.domain.model.UserInfo
import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock
import kotlin.time.Instant

class SettingsStorageImpl(
    private val settings: Settings
) : SettingsStorage {
    override fun saveUserInfo(
        token: String, username: String, role: String, userId: Long?, schoolId: Long?
    ) {
        settings.putString(KEY_TOKEN, token)
        settings.putString(KEY_USERNAME, username)
        settings.putString(KEY_ROLE, role)
        if (userId != null) settings.putLong(KEY_USER_ID, userId)
        if (schoolId != null) settings.putLong(KEY_SCHOOL_ID, schoolId)
    }

    override fun getUserInfo(): UserInfo {
        val storedToken = settings.getStringOrNull(KEY_TOKEN)
        val storedUsername = settings.getString(KEY_USERNAME, "Utilisateur")
        val storedRole = settings.getString(KEY_ROLE, "Role")
        val storedUserId = settings.getLongOrNull(KEY_USER_ID)
        val storedSchoolId = settings.getLongOrNull(KEY_SCHOOL_ID)
        return UserInfo(storedToken, storedUsername, storedRole, storedUserId, storedSchoolId)
    }

    override fun getToken(): String? {
        val token = settings.getStringOrNull(KEY_TOKEN)
        return token
    }

    override fun clearUserInfo() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USERNAME)
        settings.remove(KEY_ROLE)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_SCHOOL_ID)
        settings.remove(KEY_SCHOOL_DATA) // Clear school data as well
    }

    override fun isTokenExpired(): Boolean {
        val expirationInstant = getTokenExpirationDate()
        val currentTime = Clock.System.now()

        if (expirationInstant == null) {
            return true // If no expiration date, assume expired
        }
        val isExpired = expirationInstant <= currentTime
        return isExpired
    }

    override fun getTokenExpirationDate(): Instant? {
        val token = getToken() ?: return null

        val payload = decodePayload(token) ?: return null

        val expSeconds = payload.exp ?: return null

        val expirationInstant = Instant.fromEpochSeconds(expSeconds)
        return expirationInstant
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decodePayload(token: String): JwtPayload? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) {
                return null
            }

            val payloadBase64 = parts[1]

            // Fix padding if needed
            val padded = payloadBase64.padEnd(
                payloadBase64.length + (4 - payloadBase64.length % 4) % 4, '='
            )

            val decodedBytes = Base64.UrlSafe.decode(padded)
            val json = decodedBytes.decodeToString()

            Json.decodeFromString<JwtPayload>(json)
        } catch (e: IllegalArgumentException) {
            println("SettingsStorageImpl: decodePayload() - Error decoding JWT payload Base64: ${e.message}")
            null
        } catch (e: Exception) {
            println("SettingsStorageImpl: decodePayload() - Error parsing JWT payload JSON: ${e.message}. Token: $token")
            null
        }
    }

    override fun saveSchool(school: SchoolDTO) {
        try {
            val schoolJson = Json.encodeToString(school)
            settings.putString(KEY_SCHOOL_DATA, schoolJson)
        } catch (e: Exception) {
            println("SettingsStorageImpl: Error saving SchoolDTO: ${e.message}")
        }
    }

    override fun getSchool(): SchoolDTO? {
        return try {
            val schoolJson = settings.getStringOrNull(KEY_SCHOOL_DATA)
            if (schoolJson != null) {
                val school = Json.decodeFromString<SchoolDTO>(schoolJson)
                school
            } else {
                null
            }
        } catch (e: Exception) {
            println("SettingsStorageImpl: Error retrieving SchoolDTO: ${e.message}")
            null
        }
    }

    override fun saveActiveAcademicYear(academicYearDTO: AcademicYearDTO) {
        try {
            val schoolJson = Json.encodeToString(academicYearDTO)
            settings.putString(KEY_ACTIVE_ACADEMIC_YEAR, schoolJson)
        } catch (e: Exception) {
            println("SettingsStorageImpl: Error saving academcic year: ${e.message}")
        }
    }

    override fun getActiveAcademicYear(): AcademicYearDTO? {
        return try {
            val academicYearJson = settings.getStringOrNull(KEY_ACTIVE_ACADEMIC_YEAR)
            if (academicYearJson != null) {
                val academicYearJson = Json.decodeFromString<AcademicYearDTO>(academicYearJson)
                academicYearJson
            } else {
                null
            }
        } catch (e: Exception) {
            println("SettingsStorageImpl: Error retrieving Academic Year: ${e.message}")
            null
        }
    }
}
