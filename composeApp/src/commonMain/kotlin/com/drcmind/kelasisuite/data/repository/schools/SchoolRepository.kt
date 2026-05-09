package com.drcmind.kelasisuite.data.repository.schools

import com.drcmind.kelasisuite.domain.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    fun getSchool(schoolId: Long): Flow<Resource<SchoolDTO>>
    fun getSchoolSections(schoolId: Long): Flow<Resource<List<SchoolSectionDTO>>>
    fun getClasses(schoolId: Long): Flow<Resource<List<SchoolClassDTO>>>
    fun createClass(request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>>
    fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>>
    fun deleteClass(classId: Long): Flow<Resource<Unit>>
}