package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChildDTO(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val profilePicture: String? = null,
    val className: String,
    val overallAverage: Double? = null,
    val status: String // e.g. "Présent", "Absent", "Retard"
)

@Serializable
data class AttendanceDTO(
    val id: Long,
    val date: String,
    val status: String, // e.g. "Présent", "Absent", "Retard"
    val remark: String? = null
)

@Serializable
data class GradeDTO(
    val courseName: String,
    val term1: Double?,
    val term2: Double?,
    val term3: Double?,
    val term4: Double?,
    val exam1: Double?,
    val exam2: Double?,
    val finalAverage: Double?
)
