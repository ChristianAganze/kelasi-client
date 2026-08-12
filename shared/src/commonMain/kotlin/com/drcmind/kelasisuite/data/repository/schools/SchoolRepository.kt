package com.drcmind.kelasisuite.data.repository.schools

import com.drcmind.kelasisuite.data.datasource.remote.dto.AcademicYearDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ClassLogReviewDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateScheduleEntryDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeLevelDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.MajorDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.PreparationReviewDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ProgramRadarDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ScheduleEntryDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentDTO
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
    fun getEvaluationPeriodsBySchool(): Flow<Resource<Map<String, List<EvaluationPeriodDTO>>>>
    fun getProgramRadar(): Flow<Resource<ProgramRadarDto>>

    fun getPreparationsForReview(): Flow<Resource<List<PreparationReviewDto>>>
    fun validatePreparation(preparationId: Long, comment: String?): Flow<Resource<PreparationReviewDto>>
    fun rejectPreparation(preparationId: Long, comment: String?): Flow<Resource<PreparationReviewDto>>

    fun getClassLogsForReview(): Flow<Resource<List<ClassLogReviewDto>>>
    fun signClassLog(classLogId: Long): Flow<Resource<ClassLogReviewDto>>
    fun createClass(request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>>
    fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>>
    fun deleteClass(classId: Long): Flow<Resource<Unit>>

    fun getAssignmentsForClass(classId: Long, academicYearId: Long): Flow<Resource<List<TeachingAssignmentDTO>>>

    // SchoolSectionConfig Endpoints
    fun createSchoolSectionConfig(configDto: SchoolSectionConfigDto): Flow<Resource<SchoolSectionConfigDto>>
    fun getSchoolSectionConfigById(id: Long): Flow<Resource<SchoolSectionConfigDto>>
    fun getAllSchoolSectionConfigsBySchool(): Flow<Resource<List<SchoolSectionConfigDto>>>
    fun updateSchoolSectionConfig(id: Long, configDto: SchoolSectionConfigDto): Flow<Resource<SchoolSectionConfigDto>>
    fun deleteSchoolSectionConfig(id: Long): Flow<Resource<Unit>>

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

    // ScheduleEntry Endpoints
    fun createScheduleEntry(entryDto: CreateScheduleEntryDto): Flow<Resource<ScheduleEntryDto>>
    fun updateScheduleEntry(id: Long, entryDto: CreateScheduleEntryDto): Flow<Resource<ScheduleEntryDto>>
    fun deleteScheduleEntry(id: Long): Flow<Resource<Unit>>
    fun getScheduleEntriesByWeekNumber(weekNumber: Int): Flow<Resource<List<ScheduleEntryDto>>>
    fun getScheduleEntriesByLearningTimeConfigDayOfWeekAndWeekNumber(
        dayOfWeek: DayOfWeek,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>>
    fun getScheduleEntriesByTeachingAssignmentIdAndWeekNumber(
        teachingAssignmentId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>>
    fun getScheduleEntriesBySchoolIdAndWeekNumber(
        schoolId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>>
    fun getScheduleEntriesBySchoolSectionIdAndWeekNumber(
        schoolSectionId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>>
    fun getScheduleEntriesByTemplateSchoolSectionIdAndWeekNumber(
        templateSchoolSectionId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>>
    fun getScheduleEntriesByTemplateSectionIdAndWeekNumber(
        templateSectionId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>>
    fun getScheduleEntriesByGradeLevelIdAndWeekNumber(
        gradeLevelId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>>
    fun getScheduleEntriesBySchoolClassIdAndWeekNumber(
        schoolClassId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>>
    fun duplicateScheduleEntries(
        sourceWeek: Int,
        classId: Long,
        targetWeeks: List<Int>
    ): Flow<Resource<Unit>>
    fun getWeeklySchedule(
        weekNumber: Int,
        classId: Long
    ): Flow<Resource<List<ScheduleEntryDto>>>
    fun clearWeek(
        weekNumber: Int,
        classId: Long
    ): Flow<Resource<Unit>>
}