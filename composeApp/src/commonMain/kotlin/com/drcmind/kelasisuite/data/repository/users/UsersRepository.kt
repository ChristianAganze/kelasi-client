package com.drcmind.kelasisuite.data.repository.users

import com.drcmind.kelasisuite.domain.dto.UserDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun getUserBySchoolId(schoolId: Long): Flow<Resource<List<UserDTO>>>
}