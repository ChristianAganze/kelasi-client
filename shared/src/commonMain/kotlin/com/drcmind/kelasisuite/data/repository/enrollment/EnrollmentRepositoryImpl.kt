package com.drcmind.kelasisuite.data.repository.enrollment

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class EnrollmentRepositoryImpl(
    private val schoolAdminApiService: SchoolAdminApiService,
    private val settingsStorage: SettingsStorage
) : EnrollmentRepository {
    override fun createEnrollment(createEnrollmentRequest: EnrollmentRequest): Flow<Resource<StudentDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = schoolAdminApiService.enrollStudent(createEnrollmentRequest)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getEnrolledStudents(): Flow<Resource<List<EnrollmentDto>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId ?: return@flow
            val academicYearId = settingsStorage.getActiveAcademicYear()?.id

            println("AAAAAAAAAAAAA : $schoolId")
            println("BBBBBBBBBBBBB : $academicYearId")
            if (academicYearId == null) {
                emit(Resource.Error(message = "Aucune année académique active disponible."))
                return@flow
            }
            val response = schoolAdminApiService.getEnrolledStudents(schoolId, academicYearId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}