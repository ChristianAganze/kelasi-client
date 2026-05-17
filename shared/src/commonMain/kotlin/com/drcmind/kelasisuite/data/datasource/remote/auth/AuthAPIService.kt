package com.drcmind.kelasisuite.data.datasource.remote.auth

import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import com.drcmind.kelasisuite.domain.dto.UserResponseDTO

interface AuthAPIService {
    suspend fun login(loginRequest: LoginRequest): LoginResponse
    suspend fun getUserMe(): UserResponseDTO
}