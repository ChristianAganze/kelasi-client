package com.drcmind.kelasisuite.data.datasource.remote.schools

import com.drcmind.kelasisuite.domain.dto.*

interface SchoolsAPIService {
    suspend fun getSchool(schoolId: Long): SchoolDTO
    suspend fun getSchoolSections(schoolId: Long): List<SchoolSectionDTO>
    suspend fun getSectionBySchoolSectionAndSchool(schoolSectionId: Long, schoolId: Long) : List<SectionDTO>
    suspend fun getOfferedMajorBySchoolAndBySection(schoolId: Long, sectionId: Long) : List<MajorDto>
    suspend fun getOfferedMajorsForSchool(schoolId: Long) : List<MajorDto>
    suspend fun getGradeLevelsBySchoolAndByMajor(schoolId: Long, majorId: Long) : List<GradeLevelDTO>
    suspend fun getClassesForSchool(schoolId: Long): List<SchoolClassDTO>
    suspend fun getClassesForSchoolAndMajor(schoolId: Long, majorId: Long) : List<SchoolClassDTO>
    suspend fun getClassesBySchoolAndGradeLevel(schoolId: Long, gradeLevelId: Long) : List<SchoolClassDTO>
    suspend fun createClass(request: CreateClassFromTemplateRequest): SchoolClassDTO
    suspend fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): SchoolClassDTO
    suspend fun deleteClass(classId: Long)
    suspend fun getAcademicYears() : List<AcademicYearDTO>
    suspend fun getEvaluationPeriodsBySchool(schoolId: Long) : List<EvaluationPeriodBySchoolDTO>
}