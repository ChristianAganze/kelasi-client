package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClassLogReviewDto(
    val id: Long,
    val teachingAssignmentId: Long,
    val teacherName: String = "",
    val className: String = "",
    val subject: String,
    val date: String,
    val timeSlot: String = "",
    val taughtSubject: String = "",
    val homework: String = "",
    val teacherSignature: Boolean = false,
    val adminSignature: Boolean = false
)
