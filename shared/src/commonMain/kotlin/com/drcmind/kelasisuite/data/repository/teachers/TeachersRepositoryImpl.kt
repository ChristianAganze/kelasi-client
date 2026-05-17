package com.drcmind.kelasisuite.data.repository.teachers

import com.drcmind.kelasisuite.data.datasource.remote.teachers.TeachersAPIService
import com.drcmind.kelasisuite.domain.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.domain.dto.TeacherProfileRequest
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TeachersRepositoryImpl(
    private val apiService: TeachersAPIService
) : TeachersRepository {

    override fun createTeacher(createRequest: TeacherProfileRequest): Flow<Resource<TeacherProfileDTO>> {
        return flow {
            emit(Resource.Loading())
            val creationResponse = apiService.createTeacher(createRequest)
            emit(Resource.Success(creationResponse))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun updateTeacher(
        teacherId: Long,
        updateRequest: TeacherProfileRequest
    ): Flow<Resource<TeacherProfileDTO>> {
        return flow {
            emit(Resource.Loading())
            val updateResponse = apiService.updateTeacher(teacherId, updateRequest)
            emit(Resource.Success(updateResponse))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getTeachers(schoolId: Long): Flow<Resource<List<TeacherProfileDTO>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getTeachers(schoolId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getTeacher(teacherId: Long): Flow<Resource<TeacherProfileDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getTeacher(teacherId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}
