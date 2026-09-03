package com.drcmind.kelasisuite.data.repository.auth

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.SystemApiService
import com.drcmind.kelasisuite.data.datasource.remote.dto.LoginRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.LoginResponse
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserResponseDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val settingStorage: SettingsStorage,
    private val systemApiService: SystemApiService
) : AuthRepository {
    override fun login(loginRequest: LoginRequest): Flow<Resource<LoginResponse>> {
        return flow {
            emit(Resource.Loading())
            val loginResponse = systemApiService.login(loginRequest)
            settingStorage.saveUserInfo(
                token = loginResponse.token,
                username = loginResponse.username,
                role = loginResponse.roles.first(),
            )
            try {
                val userResponse = systemApiService.getUserMe()
                val userInfoResponse = try {
                    systemApiService.getUser(userResponse.id)
                } catch (e: Exception) {
                    null
                }
                val schoolId = userInfoResponse?.schoolId ?: userResponse.schoolId
                if (schoolId != null && schoolId > 0) {
                    try {
                        val school = systemApiService.getSchool(schoolId)
                        settingStorage.saveSchool(school)
                    } catch (e: Exception) {
                        println("AuthRepositoryImpl: Failed to fetch school: ${e.message}")
                    }
                }
                val resolvedFirstName = userInfoResponse?.firstName?.takeIf { it.isNotBlank() }
                    ?: userResponse.firstName?.takeIf { it.isNotBlank() }
                val resolvedLastName = userInfoResponse?.lastName?.takeIf { it.isNotBlank() }
                    ?: userResponse.lastName?.takeIf { it.isNotBlank() }
                val resolvedUsername = userResponse.username.ifBlank { loginResponse.username }
                val resolvedRole = userResponse.roles.firstOrNull() ?: loginResponse.roles.first()

                settingStorage.saveUserInfo(
                    token = loginResponse.token,
                    username = resolvedUsername,
                    role = resolvedRole,
                    userId = userResponse.id,
                    schoolId = schoolId,
                    firstName = resolvedFirstName,
                    lastName = resolvedLastName
                )
            } catch (e: Exception) {
                println("AuthRepositoryImpl: Failed to fetch profile after login: ${e.message}")
            }
            emit(Resource.Success(loginResponse))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun fetchAndSaveCurrentUser(): Flow<Resource<UserResponseDTO>> {
        return flow {
            emit(Resource.Loading())
            val userResponse = systemApiService.getUserMe()
            val userInfo = settingStorage.getUserInfo()
            val userInfoResponse = try {
                systemApiService.getUser(userResponse.id)
            } catch (e: Exception) {
                null
            }
            val schoolId = userInfoResponse?.schoolId ?: userResponse.schoolId ?: userInfo.schoolId
            if (schoolId != null && schoolId > 0) {
                try {
                    val school = systemApiService.getSchool(schoolId)
                    settingStorage.saveSchool(school)
                } catch (e: Exception) {
                    println("AuthRepositoryImpl: Failed to fetch school: ${e.message}")
                }
            }
            val resolvedFirstName = userInfoResponse?.firstName?.takeIf { it.isNotBlank() }
                ?: userResponse.firstName?.takeIf { it.isNotBlank() }
                ?: userInfo.firstName
            val resolvedLastName = userInfoResponse?.lastName?.takeIf { it.isNotBlank() }
                ?: userResponse.lastName?.takeIf { it.isNotBlank() }
                ?: userInfo.lastName
            val resolvedUsername = userResponse.username.ifBlank { userInfo.username ?: "" }
            val resolvedRole = userResponse.roles.firstOrNull() ?: userInfo.role ?: ""

            settingStorage.saveUserInfo(
                token = userInfo.token ?: "",
                username = resolvedUsername,
                role = resolvedRole,
                userId = userResponse.id,
                schoolId = schoolId,
                firstName = resolvedFirstName,
                lastName = resolvedLastName
            )
            emit(Resource.Success(userResponse))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}