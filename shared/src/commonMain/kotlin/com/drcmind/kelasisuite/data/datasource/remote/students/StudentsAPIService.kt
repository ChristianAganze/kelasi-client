package com.drcmind.kelasisuite.data.datasource.remote.students

import com.drcmind.kelasisuite.domain.dto.StudentCreationRequest
import com.drcmind.kelasisuite.domain.dto.StudentDTO

interface StudentsAPIService {
    suspend fun createStudent(creationRequest: StudentCreationRequest): StudentDTO
    suspend fun updateStudent(studentId: Long, updateRequest: StudentCreationRequest): StudentDTO
    suspend fun getStudents(schoolId: Long): List<StudentDTO>
    suspend fun getStudent(studentId: Long): StudentDTO
}