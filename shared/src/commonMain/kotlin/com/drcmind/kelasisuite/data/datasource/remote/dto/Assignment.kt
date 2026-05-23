package com.drcmind.kelasisuite.domain.dto

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
