package com.drcmind.kelasisuite.data.datasource.remote.teachers

import com.drcmind.kelasisuite.domain.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.domain.dto.TeacherProfileRequest

interface TeachersAPIService {
        suspend fun createTeacher(creationRequest: TeacherProfileRequest): TeacherProfileDTO
    suspend fun getTeachers(schoolId: Long): List<TeacherProfileDTO>
    suspend fun getTeacher(teacherId: Long): TeacherProfileDTO
}