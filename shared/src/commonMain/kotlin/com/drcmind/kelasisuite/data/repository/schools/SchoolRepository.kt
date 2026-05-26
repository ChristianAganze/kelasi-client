package com.drcmind.kelasisuite.data.repository.schools

import com.drcmind.kelasisuite.data.datasource.remote.dto.AcademicYearDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodBySchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeLevelDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.MajorDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SectionDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek

interface SchoolRepository {
    fun getSchool(): Flow<Resource<SchoolDTO>>
    fun saveSchoolLocally(school: SchoolDTO)
    fun saveActiveAcademicYearLocally(academicYearDTO: AcademicYearDTO)
    fun getSchoolSections(): Flow<Resource<List<SchoolSectionDTO>>>
    fun getSectionBySchoolSectionAndSchool(schoolSectionId: Long): Flow<Resource<List<SectionDTO>>>
    fun getOfferedMajorsForSchoolAndSection(sectionId: Long): Flow<Resource<List<MajorDto>>>
    fun getOfferedMajorsForSchool(): Flow<Resource<List<MajorDto>>>
    fun getGradeLevelsBySchoolAndByMajor(majorId: Long): Flow<Resource<List<GradeLevelDTO>>>
    fun getClassesForSchoolAndMajor(majorId: Long): Flow<Resource<List<SchoolClassDTO>>>
    fun getClassesForSchool(): Flow<Resource<List<SchoolClassDTO>>>
    fun getClassesBySchoolAndGradeLevel(gradeLevelId: Long): Flow<Resource<List<SchoolClassDTO>>>
    fun getAcademicYears(): Flow<Resource<List<AcademicYearDTO>>>
    fun getActiveAcademicYear(): AcademicYearDTO?
    fun getEvaluationPeriodsBySchool(): Flow<Resource<List<EvaluationPeriodBySchoolDTO>>>
    fun createClass(request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>>
    fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>>
    fun deleteClass(classId: Long): Flow<Resource<Unit>>

    // LearningTimeConfig Endpoints
    fun createLearningTimeConfig(configDto: LearningTimeConfigDto): Flow<Resource<LearningTimeConfigDto>>
    fun getLearningTimeConfigById(id: Long): Flow<Resource<LearningTimeConfigDto>>
    fun getAllLearningTimeConfigs(): Flow<Resource<List<LearningTimeConfigDto>>>
    fun getLearningTimeConfigsBySchoolSectionConfigId(schoolSectionConfigId: Long): Flow<Resource<List<LearningTimeConfigDto>>>
    fun getLearningTimeConfigsByDayOfWeekAndSchoolSectionConfigId(
        dayOfWeek: DayOfWeek,
        schoolSectionConfigId: Long
    ): Flow<Resource<List<LearningTimeConfigDto>>>
    fun updateLearningTimeConfig(id: Long, configDto: LearningTimeConfigDto): Flow<Resource<LearningTimeConfigDto>>
    fun deleteLearningTimeConfig(id: Long): Flow<Resource<Unit>>
}