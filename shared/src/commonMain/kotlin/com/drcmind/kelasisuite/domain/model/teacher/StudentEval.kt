package com.drcmind.kelasisuite.domain.model.teacher

data class StudentEval(
    val id: String,
    val firstName: String,
    val lastName: String,
    val attendance: AttendanceStatus = AttendanceStatus.PRESENT,
    val grade: String = "" // String to allow empty or out-of-scale inputs before validation
)

enum class AttendanceStatus {
    PRESENT, ABSENT, LATE, EXCUSED
}
