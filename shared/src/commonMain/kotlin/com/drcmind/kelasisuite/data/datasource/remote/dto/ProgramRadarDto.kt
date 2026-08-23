package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProgramRadarDto(
    val academicYearId: Long,
    val academicYearLabel: String = "",
    val weekNumber: Int,
    val classes: List<ProgramRadarClassDto>
)

@Serializable
data class ProgramRadarClassDto(
    val classId: Long,
    val className: String,
    val sectionLabel: String = "",
    val studentsCount: Int = 0,
    val subjects: List<ProgramRadarSubjectDto>
)

@Serializable
data class ProgramRadarSubjectDto(
    val subjectName: String,
    val nationalTarget: Float,
    val realized: Float
)
