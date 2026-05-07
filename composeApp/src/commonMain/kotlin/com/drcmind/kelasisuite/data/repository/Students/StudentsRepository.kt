package com.drcmind.kelasisuite.data.repository.Students

import com.drcmind.kelasisuite.domain.dto.GetStudentRequest
import com.drcmind.kelasisuite.domain.dto.GetStudentResponse
import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface StudentsRepository {
    fun getStudent(loginRequest: GetStudentRequest): Flow<Resource<GetStudentResponse>>
}