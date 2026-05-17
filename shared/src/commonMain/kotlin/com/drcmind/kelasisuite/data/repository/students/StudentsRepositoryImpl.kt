package com.drcmind.kelasisuite.data.repository.students

import com.drcmind.kelasisuite.data.datasource.remote.students.StudentsAPIService
import com.drcmind.kelasisuite.domain.dto.StudentCreationRequest
import com.drcmind.kelasisuite.domain.dto.StudentDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class StudentsRepositoryImpl(
    private val studentsAPIService: StudentsAPIService
) : StudentsRepository {
    override fun createStudent(createRequest: StudentCreationRequest): Flow<Resource<StudentDTO>> {
        return flow {
            emit(Resource.Loading())
            val creationResponse = studentsAPIService.createStudent(createRequest)
            emit(Resource.Success(creationResponse))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun updateStudent(
        studentId: Long,
        updateRequest: StudentCreationRequest
    ): Flow<Resource<StudentDTO>> {
        return flow {
            emit(Resource.Loading())
            val updateResponse = studentsAPIService.updateStudent(studentId, updateRequest)
            emit(Resource.Success(updateResponse))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getStudents(schoolId: Long): Flow<Resource<List<StudentDTO>>> {
        return flow {
            emit(Resource.Loading())
            val response = studentsAPIService.getStudents(schoolId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getStudent(studentId: Long): Flow<Resource<StudentDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = studentsAPIService.getStudent(studentId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}
