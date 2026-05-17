package com.drcmind.kelasisuite.data.repository.profile

import com.drcmind.kelasisuite.data.datasource.remote.profile.ProfileAPIService
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.dto.UserDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ProfileRepositoryImpl(
    private val apiService: ProfileAPIService
) : ProfileRepository {
    override fun getUser(userId: Long): Flow<Resource<UserDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getUser(userId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getSchool(schoolId: Long): Flow<Resource<SchoolDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getSchool(schoolId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}