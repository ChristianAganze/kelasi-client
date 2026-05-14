package com.drcmind.kelasisuite.data.datasource.local.settings

import com.drcmind.kelasisuite.domain.dto.AcademicYearDTO
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.model.UserInfo
import kotlinx.serialization.Serializable
import kotlin.time.Instant

interface SettingsStorage {
    fun saveUserInfo(token: String, username: String, role: String, userId: Long? = null, schoolId: Long? = null)
    fun getUserInfo(): UserInfo
    fun getToken(): String?
    fun clearUserInfo()
    fun isTokenExpired(): Boolean
    fun getTokenExpirationDate(): Instant?
    fun saveSchool(school: SchoolDTO)
    fun getSchool(): SchoolDTO?
    fun saveActiveAcademicYear(academicYearDTO: AcademicYearDTO)
    fun getActiveAcademicYear() : AcademicYearDTO?
}