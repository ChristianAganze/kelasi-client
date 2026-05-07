package com.drcmind.kelasisuite.data.datasource.remote.Students

import com.drcmind.kelasisuite.domain.dto.GetStudentRequest
import com.drcmind.kelasisuite.domain.dto.GetStudentResponse
import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse

interface StudentsAPIService {
    suspend fun getStudent(loginRequest: GetStudentRequest): GetStudentResponse
}