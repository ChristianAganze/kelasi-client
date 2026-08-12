package com.drcmind.kelasisuite.data.repository.teacher

import com.drcmind.kelasisuite.data.datasource.remote.dto.LessonPreparationDTO
import com.drcmind.kelasisuite.data.datasource.remote.teacher.TeacherApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class PreparationRepositoryImpl(
    private val apiService: TeacherApiService
) : PreparationRepository {

    override fun createPreparation(request: LessonPreparationDTO): Flow<Resource<LessonPreparationDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.createPreparation(request)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getPreparations(teachingAssignmentId: Long): Flow<Resource<List<LessonPreparationDTO>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getPreparations(teachingAssignmentId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun submitPreparation(preparationId: Long): Flow<Resource<LessonPreparationDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.submitPreparation(preparationId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}
