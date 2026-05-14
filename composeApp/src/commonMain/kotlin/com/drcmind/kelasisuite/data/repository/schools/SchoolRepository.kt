package com.drcmind.kelasisuite.data.repository.schools

import com.drcmind.kelasisuite.domain.dto.AcademicYearDTO
import com.drcmind.kelasisuite.domain.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.domain.dto.EvaluationPeriodBySchoolDTO
import com.drcmind.kelasisuite.domain.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.domain.dto.GradeLevelDTO
import com.drcmind.kelasisuite.domain.dto.MajorDto
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.domain.dto.SectionDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    fun getSchool(): Flow<Resource<SchoolDTO>>
    fun saveSchoolLocally(school: SchoolDTO)
    fun getSchoolSections(): Flow<Resource<List<SchoolSectionDTO>>>
    fun getSectionBySchoolSectionAndSchool(schoolSectionId: Long) : Flow<Resource<List<SectionDTO>>>
    fun getOfferedMajorsForSchoolAndSection(sectionId: Long) : Flow<Resource<List<MajorDto>>>
    fun getOfferedMajorsForSchool() : Flow<Resource<List<MajorDto>>>
    fun getGradeLevelsBySchoolAndByMajor(majorId: Long) : Flow<Resource<List<GradeLevelDTO>>>
    fun getClassesForSchoolAndMajor(majorId: Long): Flow<Resource<List<SchoolClassDTO>>>
    fun getClassesForSchool(): Flow<Resource<List<SchoolClassDTO>>>
    fun getClassesBySchoolAndGradeLevel(gradeLevelId: Long) : Flow<Resource<List<SchoolClassDTO>>>
    fun getAcademicYears(): Flow<Resource<List<AcademicYearDTO>>>
    fun getActiveAcademicYear() : AcademicYearDTO?
    fun getEvaluationPeriodsBySchool(): Flow<Resource<List<EvaluationPeriodBySchoolDTO>>>
    fun createClass(request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>>
    fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>>
    fun deleteClass(classId: Long): Flow<Resource<Unit>>
}