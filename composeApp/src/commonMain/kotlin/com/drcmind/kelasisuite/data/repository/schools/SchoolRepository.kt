package com.drcmind.kelasisuite.data.repository.schools

import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    fun getSchool(schoolId: Long): Flow<Resource<SchoolDTO>>
}