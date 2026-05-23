package com.drcmind.kelasisuite.data.repository.enrollment

import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface EnrollmentRepository {
    fun createEnrollment(createEnrollmentRequest: EnrollmentRequest) : Flow<Resource<StudentDTO>>

    fun getEnrolledStudents(): Flow<Resource<List<EnrollmentDto>>>
}