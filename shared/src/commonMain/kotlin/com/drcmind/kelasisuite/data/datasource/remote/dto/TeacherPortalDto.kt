package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LessonPreparationDTO(
    val id: Long? = null,
    val teachingAssignmentId: Long,
    val scheduleEntryId: Long? = null,
    val date: String, // ISO-8601 date string
    val subject: String,
    val operationalObjective: String,
    val reference: String,
    val introPhase: String,
    val developmentPhase: String,
    val synthesisPhase: String,
    val applicationPhase: String
)

@Serializable
data class ClassLogDTO(
    val id: Long? = null,
    val teachingAssignmentId: Long,
    val scheduleEntryId: Long? = null,
    val date: String, // ISO-8601 date string
    val taughtSubject: String,
    val homework: String,
    val teacherSignature: Boolean,
    val adminSignature: Boolean
)

@Serializable
enum class EvaluationTypeDTO {
    ATTENDANCE, GRADE
}

@Serializable
data class StudentEvaluationDTO(
    val id: Long? = null,
    val studentId: Long,
    val scheduleEntryId: Long? = null,
    val teachingAssignmentId: Long,
    val type: EvaluationTypeDTO,
    val value: String, // e.g., "PRESENT", "ABSENT", "15"
    val date: String // ISO-8601 date string
)
