package com.drcmind.kelasisuite.data.datasource.remote.profile

import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.dto.UserDTO

interface ProfileAPIService {
    suspend fun getUser(userId: Long): UserDTO
    suspend fun getSchool(schoolId: Long): SchoolDTO
}