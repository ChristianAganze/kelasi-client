package com.drcmind.kelasisuite.data.repository.schools

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.auth.AuthAPIService
import com.drcmind.kelasisuite.data.datasource.remote.schools.SchoolsAPIService
import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SchoolRepositoryImpl(
    private val authAPIService: SchoolsAPIService
) : SchoolRepository {


    override fun getSchool(schoolId: Long): Flow<Resource<SchoolDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = authAPIService.getSchool(schoolId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}