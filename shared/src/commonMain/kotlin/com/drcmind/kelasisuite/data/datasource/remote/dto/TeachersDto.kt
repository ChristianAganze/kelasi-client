package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class TeacherProfileRequest(
    val userId: Long,
    val payrollId: String?,
    val address: Address,
    val qualifications: String,
    val hireDate: LocalDate,
    val maxWeeklyHours: Int,
    val resumeUrl : String?
)

@Serializable
data class TeacherProfileDTO(
    val id: Long,
    val userId: Long,
    val fullName: String,
    val address: Address,
    val payrollId: String?,
    val qualifications: String,
)

