package com.drcmind.kelasisuite.data.datasource.remote.students

import com.drcmind.kelasisuite.domain.dto.*

interface StudentsAPIService {
    suspend fun createStudent(creationRequest: StudentCreationRequest): StudentDTO
    suspend fun updateStudent(studentId: Long, updateRequest: StudentCreationRequest): StudentDTO
    suspend fun getStudents(schoolId: Long): List<StudentDTO>
    suspend fun getEnrolledStudents(schoolId: Long, academicYearId: Long): List<StudentDTO>
    suspend fun getStudentsForClass(classId: Long, academicYearId : Long): List<StudentDTO>
    suspend fun getStudent(studentId: Long): StudentDTO
    suspend fun enrollStudent(request: EnrollmentRequest): StudentDTO
    suspend fun updateEnrollment(enrollmentId: Long, request: UpdateEnrollmentRequest): StudentDTO
}