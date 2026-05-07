package com.drcmind.kelasisuite.data.repository.Auth

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.Auth.AuthAPIService
import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authAPIService: AuthAPIService, private val settingStorage: SettingsStorage
) : AuthRepository {
    override fun login(loginRequest: LoginRequest): Flow<Resource<LoginResponse>> {
        return flow {
            emit(Resource.Loading())
            val loginResponse = authAPIService.login(loginRequest)
            settingStorage.saveUserInfo(
                token = loginResponse.token,
                username = loginResponse.username,
                role = loginResponse.roles.first(),
            )
            emit(Resource.Success(loginResponse))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}