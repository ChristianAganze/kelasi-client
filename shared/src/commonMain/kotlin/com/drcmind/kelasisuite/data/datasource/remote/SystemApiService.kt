package com.drcmind.kelasisuite.data.datasource.remote

import com.drcmind.kelasisuite.data.datasource.remote.dto.LoginRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.LoginResponse
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserResponseDTO

interface SystemApiService {
    suspend fun login(loginRequest: LoginRequest): LoginResponse
    suspend fun getUserMe(): UserResponseDTO
    suspend fun getUser(userId: Long): UserDTO
    suspend fun getSchool(schoolId : Long): SchoolDTO
}