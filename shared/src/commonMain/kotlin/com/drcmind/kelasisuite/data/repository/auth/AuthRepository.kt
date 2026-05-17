package com.drcmind.kelasisuite.data.repository.auth

import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import com.drcmind.kelasisuite.domain.dto.UserResponseDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(loginRequest: LoginRequest): Flow<Resource<LoginResponse>>
    fun fetchAndSaveCurrentUser(): Flow<Resource<UserResponseDTO>>
}