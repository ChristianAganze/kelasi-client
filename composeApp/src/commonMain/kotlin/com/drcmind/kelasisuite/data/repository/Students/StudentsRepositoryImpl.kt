package com.drcmind.kelasisuite.data.repository.Students

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.Auth.AuthAPIService
import com.drcmind.kelasisuite.data.datasource.remote.Students.StudentsAPIService
import com.drcmind.kelasisuite.domain.dto.GetStudentRequest
import com.drcmind.kelasisuite.domain.dto.GetStudentResponse
import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class StudentsRepositoryImpl(
    private val studentsAPIService: StudentsAPIService
) : StudentsRepository {
    override fun getStudent(loginRequest: GetStudentRequest): Flow<Resource<GetStudentResponse>> {
        return flow {
            emit(Resource.Loading())
            val loginResponse = studentsAPIService.getStudent(loginRequest)

            emit(Resource.Success(loginResponse))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}
