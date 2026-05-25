package com.drcmind.kelasisuite.data.repository.teaching_assignments

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiService
import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentRequest
import com.drcmind.kelasisuite.domain.dto.TemplateSubjectDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AssignmentRepositoryImpl(
    private val apiService: SchoolAdminApiService,
    private val settingsStorage: SettingsStorage
) : AssignmentRepository {

    override fun getAssignmentsForClass(classId: Long): Flow<Resource<List<TeachingAssignmentDTO>>> {
        return flow {
            emit(Resource.Loading())
            val academicYearId = settingsStorage.getActiveAcademicYear()?.id
            if (academicYearId == null) {
                emit(Resource.Error(message = "Aucune année académique active disponible."))
                return@flow
            }
            val response = apiService.getAssignmentsForClass(classId, academicYearId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getAssignmentsForSchool(): Flow<Resource<List<TeachingAssignmentDTO>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getSchool()?.id
            val academicYearId = settingsStorage.getActiveAcademicYear()?.id
            if (academicYearId == null) {
                emit(Resource.Error(message = "Aucune année académique active disponible."))
                return@flow
            }
            val response = apiService.getAssignmentsForSchool(schoolId!!, academicYearId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getPendingAssignmentsForClass(classId: Long): Flow<Resource<List<TemplateSubjectDTO>>> {
        return flow {
            emit(Resource.Loading())
            val academicYearId = settingsStorage.getActiveAcademicYear()?.id
            if (academicYearId == null) {
                emit(Resource.Error(message = "Aucune année académique active disponible."))
                return@flow
            }
            val response = apiService.getPendingAssignmentsForClass(classId, academicYearId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun createTeachingAssignment(request: TeachingAssignmentRequest): Flow<Resource<TeachingAssignmentDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.createTeachingAssignment(request)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun deleteTeachingAssignment(assignmentId: Long): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            apiService.deleteTeachingAssignment(assignmentId)
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}
