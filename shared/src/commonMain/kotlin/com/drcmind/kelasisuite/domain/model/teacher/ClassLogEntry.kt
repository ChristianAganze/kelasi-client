package com.drcmind.kelasisuite.domain.model.teacher

data class ClassLogEntry(
    val id: String,
    val timeSlot: String,
    val className: String,
    val subject: String,
    val linkedPreparationId: String? = null,
    val linkedPreparationTitle: String? = null,
    val linkedObjective: String? = null,
    val linkedReference: String? = null,
    val status: LogStatus = LogStatus.NOT_STARTED,
    val teacherNote: String = "",
    val homework: String = ""
)

enum class LogStatus {
    NOT_STARTED, IN_PROGRESS, COMPLETED, POSTPONED
}
