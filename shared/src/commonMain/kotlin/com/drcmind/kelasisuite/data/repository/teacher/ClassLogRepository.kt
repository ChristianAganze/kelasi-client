package com.drcmind.kelasisuite.data.repository.teacher

import com.drcmind.kelasisuite.data.datasource.remote.dto.ClassLogDTO
import com.drcmind.kelasisuite.data.datasource.remote.teacher.TeacherApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface ClassLogRepository {
    fun createClassLog(request: ClassLogDTO): Flow<Resource<ClassLogDTO>>
    fun getClassLogs(teachingAssignmentId: Long): Flow<Resource<List<ClassLogDTO>>>
}

class ClassLogRepositoryImpl(
    private val apiService: TeacherApiService
) : ClassLogRepository {

    override fun createClassLog(request: ClassLogDTO): Flow<Resource<ClassLogDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.createClassLog(request)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getClassLogs(teachingAssignmentId: Long): Flow<Resource<List<ClassLogDTO>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getClassLogs(teachingAssignmentId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}
