package com.drcmind.kelasisuite.data.repository.users

import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UsersRepositoryImpl(
    private val apiService: SchoolAdminApiService
) : UsersRepository {
    override fun getUserBySchoolId(schoolId: Long): Flow<Resource<List<UserDTO>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getUserBySchoolId(schoolId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}
