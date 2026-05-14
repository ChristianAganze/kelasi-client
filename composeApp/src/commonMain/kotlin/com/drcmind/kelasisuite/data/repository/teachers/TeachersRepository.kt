package com.drcmind.kelasisuite.data.repository.teachers

import com.drcmind.kelasisuite.domain.dto.StudentCreationRequest
import com.drcmind.kelasisuite.domain.dto.StudentDTO
import com.drcmind.kelasisuite.domain.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.domain.dto.TeacherProfileRequest
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface TeachersRepository {
    fun createTeacher(createRequest: TeacherProfileRequest): Flow<Resource<TeacherProfileDTO>>
    fun updateTeacher(teacherId: Long, updateRequest: TeacherProfileRequest): Flow<Resource<TeacherProfileDTO>>
    fun getTeachers(schoolId: Long): Flow<Resource<List<TeacherProfileDTO>>>
    fun getTeacher(teacherId: Long): Flow<Resource<TeacherProfileDTO>>
}