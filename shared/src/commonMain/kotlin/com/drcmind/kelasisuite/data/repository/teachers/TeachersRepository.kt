package com.drcmind.kelasisuite.data.repository.teachers

import com.drcmind.kelasisuite.data.datasource.remote.dto.HomeroomAssignmentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.HomeroomAssignmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileRequest
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface TeachersRepository {
    fun createTeacher(createRequest: TeacherProfileRequest): Flow<Resource<TeacherProfileDTO>>
    fun updateTeacher(teacherId: Long, updateRequest: TeacherProfileRequest): Flow<Resource<TeacherProfileDTO>>
    fun getTeachers(schoolId: Long): Flow<Resource<List<TeacherProfileDTO>>>
    fun getTeacher(teacherId: Long): Flow<Resource<TeacherProfileDTO>>

    fun assignHomeroomTeacher(
         request: HomeroomAssignmentRequest
    ): Flow<Resource<HomeroomAssignmentDTO>>

    fun getHomeroomTeacherForClass(
        classId: Long,
     ): Flow<Resource<HomeroomAssignmentDTO>>

    fun getHomeroomAssignmentsForTeacher(
        teacherProfileId: Long,
     ): Flow<Resource<List<HomeroomAssignmentDTO>>>
}