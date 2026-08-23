package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
enum class PreparationReviewStatusDto {
    PENDING,
    VALIDATED,
    REJECTED
}

@Serializable
data class PreparationReviewDto(
    val id: Long,
    val teachingAssignmentId: Long,
    val teacherName: String = "",
    val className: String = "",
    val subject: String,
    val date: String,
    val operationalObjective: String,
    val reference: String = "",
    val introPhase: String,
    val developmentPhase: String,
    val synthesisPhase: String,
    val applicationPhase: String,
    val status: PreparationReviewStatusDto = PreparationReviewStatusDto.PENDING,
    val comment: String? = null
)

@Serializable
data class PreparationReviewUpdateRequest(
    val comment: String? = null
)
