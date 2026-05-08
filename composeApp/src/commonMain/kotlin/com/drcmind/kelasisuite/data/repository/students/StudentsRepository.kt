package com.drcmind.kelasisuite.data.repository.students

import com.drcmind.kelasisuite.domain.dto.StudentCreationRequest
import com.drcmind.kelasisuite.domain.dto.StudentDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface StudentsRepository {
    fun createStudent(createRequest: StudentCreationRequest): Flow<Resource<StudentDTO>>
    fun getStudents(schoolId: Long): Flow<Resource<List<StudentDTO>>>
    fun getStudent(studentId: Long): Flow<Resource<StudentDTO>>
}