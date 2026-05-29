package com.drcmind.kelasisuite.data.datasource.remote.dto

import com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.student.StudentStatus
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlin.time.Instant

//CREATE
@Serializable
data class StudentCreationRequest(
    val studentIdNumber: String, // matricule
    val sernieNumber: String?,
    val lastName: String,
    val firstName: String,
    val address: String?,
    val previousSchool: String,
    val religion: String?,
    val photoUrl: String?,
    val dateOfBirth: LocalDate?,
    val schoolId: Long
)

//OTHERS
@Serializable
data class StudentDTO(
    val id: Long,
    val studentIdNumber: String,
    val sernieNumber: String?,
    val lastName: String,
    val firstName: String,
    val fullName: String,
    val address: String?,
    val previousSchool: String,
    val religion: String?,
    val photoUrl: String?,
    val dateOfBirth: LocalDate?,
    val status: StudentStatus,
    val currentEnrollment: EnrollmentSummaryDTO? // A summary of their active enrollment
)

@Serializable
data class EnrollmentSummaryDTO(
    val enrollmentId: Long,
    val classId: Long,
    val className: String,
    val gradeLevel: String,
    val academicYear: String
)

@Serializable
data class EnrollmentRequest(
    val studentId: Long,
    val classId: Long,
    val academicYearId: Long
)

@Serializable
data class UpdateEnrollmentRequest(
    val newClassId: Long
)

@Serializable
data class CreateParentRequest(
    val userId: Long,
    val address: String,
    val occupation: String
)

@Serializable
data class ParentStudentLinkageRequest(
    val parentId: Long,
    val studentId: Long,
    val academicYearId: Long,
    val schoolId: Long,
    val isPrimaryPayer: Boolean = false,
    val relationshipType: String
)

@Serializable
data class ParentDto(
    val id: Long?,
    val userId: Long?,
    val fullName: String,
    val address: String?,
    val occupation: String?,
    val linkages: List<ParentStudentLinkageDto>,
)

@Serializable
data class ParentStudentLinkageDto(
    val id: Long?,
    val student: StudentDTO,
    val academicYearId: Long?,
    val schoolId: Long?,
    val isPrimaryPayer: Boolean,
    val relationshipType: String
)

@Serializable
data class EnrollmentDto(
    val id: Long?,
    val student: StudentSummaryDto,
    val schoolClass: SchoolClassSummaryDto,
    val academicYear: AcademicYearSummaryDto,
    val enrollmentDate: Instant
)

@Serializable
data class StudentSummaryDto(
    val id: Long?,
    val firstName: String,
    val lastName: String
)

@Serializable
data class SchoolClassSummaryDto(
    val id: Long?,
    val name: String,
    val gradeLevel: String,
    val major: String,
    val section: String,
    val schoolSection: String
)

@Serializable
data class AcademicYearSummaryDto(
    val id: Long?,
    val label: String
)


@Serializable
data class LearningTimeConfigDto(
    val id: Long?,
    val label: String,
    val startDayHourTime: String,
    val endDayHourTime: String,
    val dayOfWeek: DayOfWeek,
    val schoolSectionConfigId: Long
)

@Serializable
data class ScheduleEntryDto(
    val id: Long?,
    val learningTimeConfigId: Long,
    val teachingAssignmentId: Long,
    val weekNumber: Int
)
@Serializable
data class SchoolSectionConfigDto(
    val id: Long?,
    val dayStartTime: LocalTime,
    val dayEndTime: LocalTime,
    val schoolSectionId: Long
)