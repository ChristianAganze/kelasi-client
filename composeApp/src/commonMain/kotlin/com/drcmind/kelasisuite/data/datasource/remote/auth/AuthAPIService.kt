package com.drcmind.kelasisuite.data.datasource.remote.auth

import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse

interface AuthAPIService {
    suspend fun login(loginRequest: LoginRequest): LoginResponse
}