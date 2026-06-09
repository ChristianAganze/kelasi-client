package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeachingAssignmentRequest(
    val classId: Long,
    val subjectId: Long,
    val teacherProfileId: Long,
    val academicYearId: Long
)

@Serializable
data class TeachingAssignmentDTO(
    val id: Long,
    val classId: Long,
    val className: String,
    val subjectId: Long,
    val subjectName: String,
    val subjectCode: String,
    val teacherId: Long,
    val teacherName: String,
    val academicYearId: Long
)

@Serializable
data class TemplateSubjectDTO(
    val id: Long,
    val code: String,
    val name: String,
    val domain: String?,
    val subDomain: String?
)
@Serializable
enum class AssignmentStatus {
    ASSIGNED,
    PENDING
}

@Serializable
data class CombinedAssignmentModel(
    val id: Long,               // Represents either teachingAssignmentId or templateSubjectId
    val subjectId: Long,
    val subjectName: String,
    val subjectCode: String,
    val status: AssignmentStatus,

    // Optional/Nullable properties because they only exist if ASSIGNED
    val teacherId: Long? = null,
    val teacherName: String? = null,
    val classId: Long? = null,
    val className: String? = null,
    val academicYearId: Long? = null,

    // Optional/Nullable properties because they only exist if PENDING
    val domain: String? = null,
    val subDomain: String? = null
)