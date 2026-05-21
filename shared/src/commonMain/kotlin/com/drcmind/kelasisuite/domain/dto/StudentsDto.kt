package com.drcmind.kelasisuite.domain.dto

import com.drcmind.kelasisuite.ui.schooladmin.students.StudentStatus
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

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
    val userId : Long,
    val address : String,
    val occupation : String
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


