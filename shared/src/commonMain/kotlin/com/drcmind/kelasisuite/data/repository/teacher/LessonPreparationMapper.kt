package com.drcmind.kelasisuite.data.repository.teacher

import com.drcmind.kelasisuite.data.datasource.remote.dto.LessonPreparationDTO
import com.drcmind.kelasisuite.domain.model.teacher.LessonPreparation
import com.drcmind.kelasisuite.domain.model.teacher.PrepHeader
import com.drcmind.kelasisuite.domain.model.teacher.PrepSteps
import com.drcmind.kelasisuite.domain.model.teacher.PreparationStatus
import com.drcmind.kelasisuite.domain.model.teacher.StepDetails

fun LessonPreparationDTO.toLessonPreparation(
    branch: String,
    className: String
): LessonPreparation = LessonPreparation(
    id = id?.toString() ?: "",
    header = PrepHeader(
        branch = branch,
        subBranch = "",
        className = className,
        revisionSubject = "",
        lessonSubject = subject,
        operationalObjective = operationalObjective,
        didacticMaterial = "",
        bibliography = reference
    ),
    steps = PrepSteps(
        introduction = StepDetails("", "", introPhase),
        development = StepDetails("", "", developmentPhase),
        synthesis = StepDetails("", "", synthesisPhase),
        application = StepDetails("", "", applicationPhase)
    ),
    status = status.toPreparationStatus(),
    dateCreated = date
)

fun String.toPreparationStatus(): PreparationStatus = when (uppercase()) {
    "SUBMITTED" -> PreparationStatus.SUBMITTED
    "APPROVED" -> PreparationStatus.APPROVED
    "REJECTED" -> PreparationStatus.REJECTED
    "ARCHIVED" -> PreparationStatus.ARCHIVED
    "READY" -> PreparationStatus.APPROVED
    else -> PreparationStatus.DRAFT
}
