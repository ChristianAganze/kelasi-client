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
                val userInfoResponse = systemApiService.getUser (userResponse.id)
                val school = systemApiService.getSchool(userInfoResponse.schoolId!!)
                settingStorage.saveSchool(school)
                settingStorage.saveUserInfo(
                    token = loginResponse.token,
                    username = userResponse.username,
                    role = userResponse.roles.firstOrNull() ?: loginResponse.roles.first(),
                    userId = userResponse.id,
                    schoolId = userInfoResponse.schoolId
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
            val userInfoResponse = systemApiService.getUser (userResponse.id)
            settingStorage.saveUserInfo(
                token = userInfo.token ?: "",
                username = userResponse.username,
                role = userResponse.roles.firstOrNull() ?: userInfo.role ?: "",
                userId = userResponse.id,
                schoolId = userInfoResponse.schoolId
            )
            emit(Resource.Success(userResponse))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}