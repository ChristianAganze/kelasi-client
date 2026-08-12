package com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin

import com.drcmind.kelasisuite.data.datasource.remote.dto.AcademicYearDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateParentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateScheduleEntryDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ClassLogReviewDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeLevelDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.HomeroomAssignmentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.HomeroomAssignmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.MajorDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentStudentLinkageDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentStudentLinkageRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.PreparationReviewDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.PreparationReviewUpdateRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.ProgramRadarDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ScheduleEntryDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentCreationRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.UpdateEnrollmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.TemplateSubjectDTO
import kotlinx.datetime.DayOfWeek

interface SchoolAdminApiService {
    suspend fun getParentsBySchool(schoolId: Long): List<ParentDto>
    suspend fun createParent(request: CreateParentRequest): ParentDto
    suspend fun linkStudentToParent(linkageRequest: ParentStudentLinkageRequest): ParentStudentLinkageDto
    suspend fun unlinkStudentFromParent(linkageId: Long)
    suspend fun updateParent(parentId: Long, createParentRequest: CreateParentRequest): ParentDto
    suspend fun deleteParent(parentId: Long)
    suspend fun getParentById(parentId: Long): ParentDto

    suspend fun getSchool(schoolId: Long): SchoolDTO
    suspend fun getSchoolSections(schoolId: Long): List<SchoolSectionDTO>
    suspend fun getSectionBySchoolSectionAndSchool(schoolSectionId: Long, schoolId: Long): List<SectionDTO>
    suspend fun getOfferedMajorBySchoolAndBySection(schoolId: Long, sectionId: Long): List<MajorDto>
    suspend fun getOfferedMajorsForSchool(schoolId: Long): List<MajorDto>
    suspend fun getGradeLevelsBySchoolAndByMajor(schoolId: Long, majorId: Long): List<GradeLevelDTO>
    suspend fun getClassesForSchool(schoolId: Long): List<SchoolClassDTO>
    suspend fun getClassesForSchoolAndMajor(schoolId: Long, majorId: Long): List<SchoolClassDTO>
    suspend fun getClassesBySchoolAndGradeLevel(schoolId: Long, gradeLevelId: Long): List<SchoolClassDTO>
    suspend fun createClass(request: CreateClassFromTemplateRequest): SchoolClassDTO
    suspend fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): SchoolClassDTO
    suspend fun deleteClass(classId: Long)
    suspend fun getAcademicYears(): List<AcademicYearDTO>
    suspend fun getEvaluationPeriodsBySchool(schoolId: Long): Map<String, List<EvaluationPeriodDTO>>

    suspend fun getProgramRadar(schoolId: Long, academicYearId: Long): ProgramRadarDto

    suspend fun getPreparationsForReview(schoolId: Long, academicYearId: Long): List<PreparationReviewDto>
    suspend fun validatePreparation(preparationId: Long, request: PreparationReviewUpdateRequest): PreparationReviewDto
    suspend fun rejectPreparation(preparationId: Long, request: PreparationReviewUpdateRequest): PreparationReviewDto

    suspend fun getClassLogsForReview(schoolId: Long, academicYearId: Long): List<ClassLogReviewDto>
    suspend fun signClassLog(classLogId: Long): ClassLogReviewDto

    suspend fun createStudent(creationRequest: StudentCreationRequest): StudentDTO
    suspend fun updateStudent(studentId: Long, updateRequest: StudentCreationRequest): StudentDTO
    suspend fun getStudents(schoolId: Long): List<StudentDTO>
    suspend fun getEnrolledStudents(schoolId: Long, academicYearId: Long): List<EnrollmentDto>
    suspend fun getStudentsForClass(classId: Long, academicYearId: Long): List<StudentDTO>
    suspend fun getStudent(studentId: Long): StudentDTO
    suspend fun enrollStudent(request: EnrollmentRequest): StudentDTO
    suspend fun updateEnrollment(enrollmentId: Long, request: UpdateEnrollmentRequest): StudentDTO

    suspend fun createTeacher(creationRequest: TeacherProfileRequest): TeacherProfileDTO
    suspend fun updateTeacher(
        teacherId: Long,
        updateRequest: TeacherProfileRequest
    ): TeacherProfileDTO

    suspend fun getTeachers(schoolId: Long): List<TeacherProfileDTO>
    suspend fun getTeacher(teacherId: Long): TeacherProfileDTO

    suspend fun getUserBySchoolId(schoolId: Long): List<UserDTO>

    suspend fun getAssignmentsForClass(classId: Long, academicYearId: Long): List<TeachingAssignmentDTO>
    suspend fun getAssignmentsForSchool(schoolId: Long, academicYearId: Long): List<TeachingAssignmentDTO>
    suspend fun getPendingAssignmentsForClass(classId: Long, academicYearId: Long): List<TemplateSubjectDTO>
    suspend fun createTeachingAssignment(request: TeachingAssignmentRequest): TeachingAssignmentDTO
    suspend fun deleteTeachingAssignment(assignmentId: Long)

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

    // SchoolSectionConfig Endpoints
    suspend fun createSchoolSectionConfig(configDto: SchoolSectionConfigDto): SchoolSectionConfigDto
    suspend fun getSchoolSectionConfigById(id: Long): SchoolSectionConfigDto
    suspend fun getAllSchoolSectionConfigsBySchool(schoolId: Long): List<SchoolSectionConfigDto>
    suspend fun updateSchoolSectionConfig(id: Long, configDto: SchoolSectionConfigDto): SchoolSectionConfigDto
    suspend fun deleteSchoolSectionConfig(id: Long)

    // LearningTimeConfig Endpoints
    suspend fun createLearningTimeConfig(configDto: LearningTimeConfigDto): LearningTimeConfigDto
    suspend fun getLearningTimeConfigById(id: Long): LearningTimeConfigDto
    suspend fun getAllLearningTimeConfigs(): List<LearningTimeConfigDto>
    suspend fun getLearningTimeConfigsBySchoolSectionConfigId(schoolSectionConfigId: Long): List<LearningTimeConfigDto>
    suspend fun getLearningTimeConfigsByDayOfWeekAndSchoolSectionConfigId(
        dayOfWeek: String,
        schoolSectionConfigId: Long
    ): List<LearningTimeConfigDto>
    suspend fun updateLearningTimeConfig(id: Long, configDto: LearningTimeConfigDto): LearningTimeConfigDto
    suspend fun deleteLearningTimeConfig(id: Long)

    // ScheduleEntry Endpoints
    suspend fun createScheduleEntry(entryDto: CreateScheduleEntryDto): ScheduleEntryDto
    suspend fun updateScheduleEntry(id: Long, entryDto: CreateScheduleEntryDto): ScheduleEntryDto
    suspend fun deleteScheduleEntry(id: Long)
    suspend fun getScheduleEntriesByWeekNumber(weekNumber: Int): List<ScheduleEntryDto>
    suspend fun getScheduleEntriesByLearningTimeConfigDayOfWeekAndWeekNumber(
        dayOfWeek: DayOfWeek,
        weekNumber: Int
    ): List<ScheduleEntryDto>
    suspend fun getScheduleEntriesByTeachingAssignmentIdAndWeekNumber(
        teachingAssignmentId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto>
    suspend fun getScheduleEntriesBySchoolIdAndWeekNumber(
        schoolId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto>
    suspend fun getScheduleEntriesBySchoolSectionIdAndWeekNumber(
        schoolSectionId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto>
    suspend fun getScheduleEntriesByTemplateSchoolSectionIdAndWeekNumber(
        templateSchoolSectionId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto>
    suspend fun getScheduleEntriesByTemplateSectionIdAndWeekNumber(
        templateSectionId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto>
    suspend fun getScheduleEntriesByGradeLevelIdAndWeekNumber(
        gradeLevelId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto>
    suspend fun getScheduleEntriesBySchoolClassIdAndWeekNumber(
        schoolClassId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto>
    suspend fun duplicateScheduleEntries(
        sourceWeek: Int,
        classId: Long,
        targetWeeks: List<Int>
    )
    suspend fun getWeeklySchedule(
        weekNumber: Int,
        classId: Long
    ): List<ScheduleEntryDto>
    suspend fun clearWeek(
        weekNumber: Int,
        classId: Long
    )
}