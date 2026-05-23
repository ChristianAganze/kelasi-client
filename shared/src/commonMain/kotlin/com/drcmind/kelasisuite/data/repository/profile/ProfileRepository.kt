package com.drcmind.kelasisuite.data.repository.profile

import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUser(userId: Long): Flow<Resource<UserDTO>>
    fun getSchool(schoolId: Long): Flow<Resource<SchoolDTO>>
}