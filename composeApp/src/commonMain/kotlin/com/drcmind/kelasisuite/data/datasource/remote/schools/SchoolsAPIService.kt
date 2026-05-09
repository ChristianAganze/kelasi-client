package com.drcmind.kelasisuite.data.datasource.remote.schools

import com.drcmind.kelasisuite.domain.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.dto.SchoolSectionDTO

interface SchoolsAPIService {
    suspend fun getSchool(schoolId: Long): SchoolDTO
    suspend fun getSchoolSections(schoolId: Long): List<SchoolSectionDTO>
    suspend fun getClasses(schoolId: Long): List<SchoolClassDTO>
    suspend fun createClass(request: CreateClassFromTemplateRequest): SchoolClassDTO
    suspend fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): SchoolClassDTO
    suspend fun deleteClass(classId: Long)
}