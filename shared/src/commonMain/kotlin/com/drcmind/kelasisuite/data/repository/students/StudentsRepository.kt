package com.drcmind.kelasisuite.data.repository.students

import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentCreationRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.UpdateEnrollmentRequest
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface StudentsRepository {
    fun createStudent(createRequest: StudentCreationRequest): Flow<Resource<StudentDTO>>
    fun updateStudent(studentId: Long, updateRequest: StudentCreationRequest): Flow<Resource<StudentDTO>>
    fun getStudents(schoolId: Long): Flow<Resource<List<StudentDTO>>>
    fun getStudentsForClass(classId: Long): Flow<Resource<List<StudentDTO>>>
    fun getStudent(studentId: Long): Flow<Resource<StudentDTO>>
    fun enrollStudent(request: EnrollmentRequest): Flow<Resource<StudentDTO>>
    fun updateEnrollment(enrollmentId: Long, request: UpdateEnrollmentRequest): Flow<Resource<StudentDTO>>
}