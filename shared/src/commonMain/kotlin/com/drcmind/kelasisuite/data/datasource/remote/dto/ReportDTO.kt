package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TermDTO(
    val id: Long,
    val name: String,
    val academicYearId: Long,
    val startDate: String,
    val endDate: String
)

@Serializable
data class ReportCardDTO(
    val id: Long? = null,
    val studentId: Long,
    val studentName: String,
    val termId: Long,
    val totalScore: Double,
    val maxScore: Double,
    val average: Double,
    val teacherRemarks: String? = null,
    val studentConduct: String? = null,
    val isPublished: Boolean = false
)
