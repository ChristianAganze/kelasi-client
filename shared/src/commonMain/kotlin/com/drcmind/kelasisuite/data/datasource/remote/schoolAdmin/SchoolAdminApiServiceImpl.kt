package com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin

import com.drcmind.kelasisuite.data.datasource.remote.dto.AcademicYearDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateParentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodBySchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeLevelDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.HomeroomAssignmentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.HomeroomAssignmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.MajorDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentStudentLinkageDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentStudentLinkageRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.ScheduleEntryDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentCreationRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.UpdateEnrollmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentRequest
import com.drcmind.kelasisuite.domain.dto.TemplateSubjectDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.datetime.DayOfWeek

class SchoolAdminApiServiceImpl(private val httpClient: HttpClient) : SchoolAdminApiService {

    override suspend fun getParentsBySchool(schoolId: Long): List<ParentDto> {
        return httpClient.get("parents/schools/$schoolId").body()
    }

    override suspend fun createParent(request: CreateParentRequest): ParentDto {
        return httpClient.post("parents") {
            setBody(request)
        }.body()
    }

    override suspend fun linkStudentToParent(linkageRequest: ParentStudentLinkageRequest): ParentStudentLinkageDto {
        return httpClient.post("parents/link-student") {
            setBody(linkageRequest)
        }.body()
    }

    override suspend fun unlinkStudentFromParent(linkageId: Long) {
        return httpClient.delete("parents/unlink-student/$linkageId").body()
    }

    override suspend fun updateParent(
        parentId: Long,
        createParentRequest: CreateParentRequest
    ): ParentDto {
        return httpClient.put("parents/$parentId") {
            setBody(createParentRequest)
        }.body()
    }

    override suspend fun deleteParent(parentId: Long) {
        return httpClient.get("parents/$parentId").body()
    }

    override suspend fun getParentById(parentId: Long): ParentDto {
        return httpClient.get("parents/$parentId").body()
    }

    override suspend fun getSchool(schoolId: Long): SchoolDTO {
        return httpClient.get("schools/$schoolId").body()
    }

    override suspend fun getSchoolSections(schoolId: Long): List<SchoolSectionDTO> {
        return httpClient.get("schools/$schoolId/school-sections").body()
    }

    override suspend fun getSectionBySchoolSectionAndSchool(
        schoolSectionId: Long,
        schoolId: Long
    ): List<SectionDTO> {
        return httpClient.get("schools/$schoolId/school-sections/$schoolSectionId/sections").body()
    }

    override suspend fun getOfferedMajorBySchoolAndBySection(
        schoolId: Long,
        sectionId: Long
    ): List<MajorDto> {
        return httpClient.get("schools/$schoolId/sections/$sectionId/majors").body()
    }

    override suspend fun getOfferedMajorsForSchool(schoolId: Long): List<MajorDto> {
        return httpClient.get("schools/$schoolId/majors").body()
    }

    override suspend fun getGradeLevelsBySchoolAndByMajor(
        schoolId: Long,
        majorId: Long
    ): List<GradeLevelDTO> {
        return httpClient.get("schools/$schoolId/majors/$majorId/grade-levels").body()
    }

    override suspend fun getClassesForSchool(schoolId: Long): List<SchoolClassDTO> {
        return httpClient.get("schools/$schoolId/classes").body()
    }

    override suspend fun getClassesForSchoolAndMajor(schoolId: Long, majorId: Long): List<SchoolClassDTO> {
        return httpClient.get("schools/$schoolId/majors/$majorId/classes").body()
    }

    override suspend fun getClassesBySchoolAndGradeLevel(
        schoolId: Long,
        gradeLevelId: Long
    ): List<SchoolClassDTO> {
        return httpClient.get("schools/$schoolId/grade-levels/$gradeLevelId/classes").body()
    }

    override suspend fun createClass(request: CreateClassFromTemplateRequest): SchoolClassDTO {
        return httpClient.post("schools/classes/from-template") {
            setBody(request)
        }.body()
    }

    override suspend fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): SchoolClassDTO {
        return httpClient.put("schools/classes/$classId") {
            setBody(request)
        }.body()
    }

    override suspend fun deleteClass(classId: Long) {
        httpClient.delete("schools/classes/$classId")
    }

    override suspend fun getAcademicYears(): List<AcademicYearDTO> {
        return httpClient.get("templates/academic-years").body()
    }

    override suspend fun getEvaluationPeriodsBySchool(schoolId: Long): List<EvaluationPeriodBySchoolDTO> {
        return httpClient.get("schools/$schoolId/evaluation-periods").body()
    }

    override suspend fun createStudent(creationRequest: StudentCreationRequest): StudentDTO {
        return httpClient.post("students") {
            setBody(creationRequest)
        }.body()
    }

    override suspend fun updateStudent(
        studentId: Long,
        updateRequest: StudentCreationRequest
    ): StudentDTO {
        return httpClient.put("students/$studentId") {
            setBody(updateRequest)
        }.body()
    }

    override suspend fun getStudents(schoolId: Long): List<StudentDTO> {
        return httpClient.get("schools/$schoolId/students/all").body()
    }

    override suspend fun getEnrolledStudents(
        schoolId: Long,
        academicYearId: Long
    ): List<EnrollmentDto> {
        return httpClient.get("schools/$schoolId/enrolled-students") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun getStudentsForClass(
        classId: Long,
        academicYearId: Long
    ): List<StudentDTO> {
        return httpClient.get("classes/$classId/students") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun getStudent(studentId: Long): StudentDTO {
        return httpClient.get("students/$studentId").body()
    }

    override suspend fun enrollStudent(request: EnrollmentRequest): StudentDTO {
        return httpClient.post("enrollments") {
            setBody(request)
        }.body()
    }

    override suspend fun updateEnrollment(
        enrollmentId: Long,
        request: UpdateEnrollmentRequest
    ): StudentDTO {
        return httpClient.put("enrollments/$enrollmentId") {
            setBody(request)
        }.body()
    }

    override suspend fun getUserBySchoolId(schoolId: Long): List<UserDTO> {
        return httpClient.get("schools/$schoolId/users").body()
    }

    override suspend fun getAssignmentsForClass(classId: Long, academicYearId: Long): List<TeachingAssignmentDTO> {
        return httpClient.get("classes/$classId/assignments") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun getAssignmentsForSchool(
        schoolId: Long,
        academicYearId: Long
    ): List<TeachingAssignmentDTO> {
        return httpClient.get("classes/$schoolId/assignments") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun getPendingAssignmentsForClass(classId: Long, academicYearId: Long): List<TemplateSubjectDTO> {
        return httpClient.get("classes/$classId/pending-assignments") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun createTeachingAssignment(request: TeachingAssignmentRequest): TeachingAssignmentDTO {
        return httpClient.post("assignments") {
            setBody(request)
        }.body()
    }

    override suspend fun deleteTeachingAssignment(assignmentId: Long) {
        httpClient.delete("assignments/$assignmentId")
    }

    override suspend fun createTeacher(creationRequest: TeacherProfileRequest): TeacherProfileDTO {
        return httpClient.post("teachers") {
            setBody(creationRequest)
        }.body()
    }

    override suspend fun updateTeacher(teacherId: Long, updateRequest: TeacherProfileRequest): TeacherProfileDTO {
        return httpClient.put("teachers/$teacherId") {
            setBody(updateRequest)
        }.body()
    }

    override suspend fun getTeachers(schoolId: Long): List<TeacherProfileDTO> {
        return httpClient.get("schools/$schoolId/teachers").body()
    }

    override suspend fun getTeacher(teacherId: Long): TeacherProfileDTO {
        return httpClient.get("teachers/$teacherId").body()
    }

    override suspend fun assignHomeroomTeacher(
        academicYearId: Long,
        request: HomeroomAssignmentRequest
    ): HomeroomAssignmentDTO {
        return httpClient.post("homeroom-assignments") {
            url {
                parameter("academicYearId", academicYearId)
            }
            setBody(request)
        }.body()
    }

    override suspend fun getHomeroomTeacher(
        classId: Long,
        academicYearId: Long
    ): HomeroomAssignmentDTO {
        return httpClient.get("classes/$classId/homeroom-teacher") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun getHomeroomAssignmentsByTeacher(
        teacherProfileId: Long,
        academicYearId: Long
    ): List<HomeroomAssignmentDTO> {
        return httpClient.get("teachers/$teacherProfileId/homeroom-assignments") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    // LearningTimeConfig Endpoints
    override suspend fun createLearningTimeConfig(configDto: LearningTimeConfigDto): LearningTimeConfigDto {
        return httpClient.post("learning-time-configs") {
            setBody(configDto)
        }.body()
    }

    override suspend fun getLearningTimeConfigById(id: Long): LearningTimeConfigDto {
        return httpClient.get("learning-time-configs/$id").body()
    }

    override suspend fun getAllLearningTimeConfigs(): List<LearningTimeConfigDto> {
        return httpClient.get("learning-time-configs").body()
    }

    override suspend fun getLearningTimeConfigsBySchoolSectionConfigId(schoolSectionConfigId: Long): List<LearningTimeConfigDto> {
        return httpClient.get("learning-time-configs/school-section-config/$schoolSectionConfigId").body()
    }

    override suspend fun getLearningTimeConfigsByDayOfWeekAndSchoolSectionConfigId(
        dayOfWeek: String,
        schoolSectionConfigId: Long
    ): List<LearningTimeConfigDto> {
        return httpClient.get("learning-time-configs/day-of-week/$dayOfWeek/school-section-config/$schoolSectionConfigId")
            .body()

    }

    override suspend fun updateLearningTimeConfig(id: Long, configDto: LearningTimeConfigDto): LearningTimeConfigDto {
        return httpClient.put("learning-time-configs/$id") {
            setBody(configDto)
        }.body()
    }

    override suspend fun deleteLearningTimeConfig(id: Long) {
        httpClient.delete("learning-time-configs/$id")
    }

    // ScheduleEntry Endpoints
    override suspend fun createScheduleEntry(entryDto: ScheduleEntryDto): ScheduleEntryDto {
        return httpClient.post("schedule-entries") {
            setBody(entryDto)
        }.body()
    }

    override suspend fun updateScheduleEntry(id: Long, entryDto: ScheduleEntryDto): ScheduleEntryDto {
        return httpClient.put("schedule-entries/$id") {
            setBody(entryDto)
        }.body()
    }

    override suspend fun deleteScheduleEntry(id: Long) {
        httpClient.delete("schedule-entries/$id")
    }

    override suspend fun getScheduleEntriesByWeekNumber(weekNumber: Int): List<ScheduleEntryDto> {
        return httpClient.get("schedule-entries/week/$weekNumber").body()
    }

    override suspend fun getScheduleEntriesByLearningTimeConfigDayOfWeekAndWeekNumber(
        dayOfWeek: DayOfWeek,
        weekNumber: Int
    ): List<ScheduleEntryDto> {
        return httpClient.get("schedule-entries/learning-time-config/day-of-week/$dayOfWeek/week/$weekNumber").body()
    }

    override suspend fun getScheduleEntriesByTeachingAssignmentIdAndWeekNumber(
        teachingAssignmentId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto> {
        return httpClient.get("schedule-entries/teaching-assignment/$teachingAssignmentId/week/$weekNumber").body()
    }

    override suspend fun getScheduleEntriesBySchoolIdAndWeekNumber(
        schoolId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto> {
        return httpClient.get("schedule-entries/school/$schoolId/week/$weekNumber").body()
    }

    override suspend fun getScheduleEntriesBySchoolSectionIdAndWeekNumber(
        schoolSectionId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto> {
        return httpClient.get("schedule-entries/school-section/$schoolSectionId/week/$weekNumber").body()
    }

    override suspend fun getScheduleEntriesByTemplateSchoolSectionIdAndWeekNumber(
        templateSchoolSectionId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto> {
        return httpClient.get("schedule-entries/template-school-section/$templateSchoolSectionId/week/$weekNumber")
            .body()
    }

    override suspend fun getScheduleEntriesByTemplateSectionIdAndWeekNumber(
        templateSectionId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto> {
        return httpClient.get("schedule-entries/template-section/$templateSectionId/week/$weekNumber").body()
    }

    override suspend fun getScheduleEntriesByGradeLevelIdAndWeekNumber(
        gradeLevelId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto> {
        return httpClient.get("schedule-entries/grade-level/$gradeLevelId/week/$weekNumber").body()
    }

    override suspend fun getScheduleEntriesBySchoolClassIdAndWeekNumber(
        schoolClassId: Long,
        weekNumber: Int
    ): List<ScheduleEntryDto> {
        return httpClient.get("schedule-entries/school-class/$schoolClassId/week/$weekNumber").body()
    }

    override suspend fun duplicateScheduleEntries(
        sourceWeek: Int,
        classId: Long,
        targetWeeks: List<Int>
    ) {
//        httpClient.post("schedule-entries/duplicate/week/$sourceWeek/class/$classId") {
//            setBody(targetWeeks)
//        }.body()
    }

    override suspend fun getWeeklySchedule(
        weekNumber: Int,
        classId: Long
    ): List<ScheduleEntryDto> {
        return httpClient.get("schedule-entries/week/$weekNumber/class/$classId").body()
    }

    override suspend fun clearWeek(
        weekNumber: Int,
        classId: Long
    ) {
        httpClient.delete("schedule-entries/week/$weekNumber/class/$classId")
    }
}