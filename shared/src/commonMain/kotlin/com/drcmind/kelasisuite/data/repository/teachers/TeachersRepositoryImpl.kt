package com.drcmind.kelasisuite.data.repository.teachers

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.teachers.TeachersAPIService
import com.drcmind.kelasisuite.domain.dto.HomeroomAssignmentDTO
import com.drcmind.kelasisuite.domain.dto.HomeroomAssignmentRequest
import com.drcmind.kelasisuite.domain.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.domain.dto.TeacherProfileRequest
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TeachersRepositoryImpl(
    private val apiService: TeachersAPIService,
    private val settingsStorage: SettingsStorage
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

    override fun assignHomeroomTeacher(
        request: HomeroomAssignmentRequest
    ): Flow<Resource<HomeroomAssignmentDTO>> {
        return flow {
            emit(Resource.Loading())
            val academicYearId = settingsStorage.getActiveAcademicYear()?.id
            if (academicYearId == null) {
                emit(Resource.Error(message = "Aucune année académique active disponible."))
                return@flow
            }
            val response = apiService.assignHomeroomTeacher(academicYearId, request)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getHomeroomTeacherForClass(
        classId: Long,
    ): Flow<Resource<HomeroomAssignmentDTO>> {
        return flow {
            emit(Resource.Loading())
            val academicYearId = settingsStorage.getActiveAcademicYear()?.id
            if (academicYearId == null) {
                emit(Resource.Error(message = "Aucune année académique active disponible."))
                return@flow
            }
            val response = apiService.getHomeroomTeacher(classId, academicYearId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getHomeroomAssignmentsForTeacher(
        teacherProfileId: Long,
    ): Flow<Resource<List<HomeroomAssignmentDTO>>> {
        return flow {
            emit(Resource.Loading())
            val academicYearId = settingsStorage.getActiveAcademicYear()?.id
            if (academicYearId == null) {
                emit(Resource.Error(message = "Aucune année académique active disponible."))
                return@flow
            }
            val response =
                apiService.getHomeroomAssignmentsByTeacher(teacherProfileId, academicYearId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}
