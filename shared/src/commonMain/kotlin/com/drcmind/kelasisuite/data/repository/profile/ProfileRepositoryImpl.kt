package com.drcmind.kelasisuite.data.repository.profile

import com.drcmind.kelasisuite.data.datasource.remote.SystemApiService
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ProfileRepositoryImpl(
    private val systemApiService: SystemApiService,
    private val schoolAdminApiService: SchoolAdminApiService
) : ProfileRepository {
    override fun getUser(userId: Long): Flow<Resource<UserDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = systemApiService.getUser(userId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getSchool(schoolId: Long): Flow<Resource<SchoolDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = schoolAdminApiService.getSchool(schoolId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}