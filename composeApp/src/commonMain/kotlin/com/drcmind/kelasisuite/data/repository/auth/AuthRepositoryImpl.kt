package com.drcmind.kelasisuite.data.repository.auth

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.auth.AuthAPIService
import com.drcmind.kelasisuite.data.datasource.remote.profile.ProfileAPIService
import com.drcmind.kelasisuite.data.repository.profile.ProfileRepository
import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import com.drcmind.kelasisuite.domain.dto.UserResponseDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authAPIService: AuthAPIService, private val settingStorage: SettingsStorage,
    //THIS ONE HAS BEEN ADDED FOR STORING THE schoolId in cache
    private val profileApiService: ProfileAPIService
) : AuthRepository {
    override fun login(loginRequest: LoginRequest): Flow<Resource<LoginResponse>> {
        return flow {
            emit(Resource.Loading())
            val loginResponse = authAPIService.login(loginRequest)
            
            // Save initial info to allow getUserMe to use the token
            settingStorage.saveUserInfo(
                token = loginResponse.token,
                username = loginResponse.username,
                role = loginResponse.roles.first(),
            )

            // Proactively fetch and save full profile (IDs)
            try {
                val userResponse = authAPIService.getUserMe()
                val userInfoResponse = profileApiService.getUser (userResponse.id)
                settingStorage.saveUserInfo(
                    token = loginResponse.token,
                    username = userResponse.username,
                    role = userResponse.roles.firstOrNull() ?: loginResponse.roles.first(),
                    userId = userResponse.id,
                    schoolId = userInfoResponse.schoolId
                )

            } catch (e: Exception) {
                // If profile fetch fails, we still have basic info, but we should log it
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
            val userResponse = authAPIService.getUserMe()
            val userInfo = settingStorage.getUserInfo()
            val userInfoResponse = profileApiService.getUser (userResponse.id)
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