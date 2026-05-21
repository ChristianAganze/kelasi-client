package com.drcmind.kelasisuite.data.datasource.remote.teachers

import com.drcmind.kelasisuite.domain.dto.HomeroomAssignmentDTO
import com.drcmind.kelasisuite.domain.dto.HomeroomAssignmentRequest
import com.drcmind.kelasisuite.domain.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.domain.dto.TeacherProfileRequest

interface TeachersAPIService {
    suspend fun createTeacher(creationRequest: TeacherProfileRequest): TeacherProfileDTO
    suspend fun updateTeacher(
        teacherId: Long,
        updateRequest: TeacherProfileRequest
    ): TeacherProfileDTO

    suspend fun getTeachers(schoolId: Long): List<TeacherProfileDTO>
    suspend fun getTeacher(teacherId: Long): TeacherProfileDTO

    suspend fun assignHomeroomTeacher(
        academicYearId: Long,
        request: HomeroomAssignmentRequest
    ): HomeroomAssignmentDTO

    suspend fun getHomeroomTeacher(
        classId: Long,
        academicYearId: Long
    ): HomeroomAssignmentDTO

    suspend fun getHomeroomAssignmentsByTeacher(
        teacherProfileId: Long,
        academicYearId: Long
    ): List<HomeroomAssignmentDTO>
}