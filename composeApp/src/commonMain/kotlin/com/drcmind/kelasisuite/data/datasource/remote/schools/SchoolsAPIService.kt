package com.drcmind.kelasisuite.data.datasource.remote.schools

import com.drcmind.kelasisuite.domain.dto.SchoolDTO

interface SchoolsAPIService {
    suspend fun getSchool(schoolId: Long): SchoolDTO
}