package com.drcmind.kelasisuite.data.datasource.remote

import com.drcmind.kelasisuite.data.datasource.remote.dto.LoginRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.LoginResponse
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserResponseDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SystemApiServiceImpl(
    private val httpClient: HttpClient
) : SystemApiService {
    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return httpClient.post("auth/login") {
            setBody(loginRequest)
        }.body()
    }

    override suspend fun getUserMe(): UserResponseDTO {
        return httpClient.get("auth/users/me").body()
    }

    override suspend fun getUser(userId: Long): UserDTO {
        return httpClient.get("users/$userId").body()
    }

    override suspend fun getSchool(schoolId: Long): SchoolDTO {
        return httpClient.get("schools/$schoolId").body()
    }

}