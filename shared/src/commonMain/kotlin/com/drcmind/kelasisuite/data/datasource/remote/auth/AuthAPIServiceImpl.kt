package com.drcmind.kelasisuite.data.datasource.remote.auth

import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import com.drcmind.kelasisuite.domain.dto.UserResponseDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthAPIServiceImpl(private val httpClient: HttpClient) : AuthAPIService {
    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return httpClient.post("auth/login") {
            setBody(loginRequest)
        }.body()
    }

    override suspend fun getUserMe(): UserResponseDTO {
        return httpClient.get("auth/users/me").body()
    }
}