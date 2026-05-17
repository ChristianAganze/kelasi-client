package com.drcmind.kelasisuite.data.datasource.remote.users

import com.drcmind.kelasisuite.domain.dto.UserDTO

interface UsersAPIService {
    suspend fun getUserBySchoolId(schoolId: Long): List<UserDTO>
}