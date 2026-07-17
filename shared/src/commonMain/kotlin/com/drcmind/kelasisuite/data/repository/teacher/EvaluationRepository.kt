package com.drcmind.kelasisuite.data.repository.teacher

import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentEvaluationDTO
import com.drcmind.kelasisuite.data.datasource.remote.teacher.TeacherApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface EvaluationRepository {
    fun submitStudentEvaluation(request: StudentEvaluationDTO): Flow<Resource<StudentEvaluationDTO>>
    fun getStudentEvaluations(teachingAssignmentId: Long): Flow<Resource<List<StudentEvaluationDTO>>>
}

class EvaluationRepositoryImpl(
    private val apiService: TeacherApiService
) : EvaluationRepository {

    override fun submitStudentEvaluation(request: StudentEvaluationDTO): Flow<Resource<StudentEvaluationDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.submitStudentEvaluation(request)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getStudentEvaluations(teachingAssignmentId: Long): Flow<Resource<List<StudentEvaluationDTO>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getStudentEvaluations(teachingAssignmentId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}
