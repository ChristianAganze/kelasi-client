package com.drcmind.kelasisuite.domain.model.teacher

import com.drcmind.kelasisuite.domain.model.common.ElectronicSignature

data class LessonPreparation(
    val id: String,
    val header: PrepHeader,
    val steps: PrepSteps,
    val status: PreparationStatus = PreparationStatus.DRAFT,
    val dateCreated: String,
    val teacherSignature: ElectronicSignature? = null,
    val validatorSignature: ElectronicSignature? = null
)

data class PrepHeader(
    val branch: String,
    val subBranch: String,
    val className: String,
    val revisionSubject: String,
    val lessonSubject: String,
    val operationalObjective: String,
    val didacticMaterial: String,
    val bibliography: String
)

data class PrepSteps(
    val introduction: StepDetails,
    val development: StepDetails,
    val synthesis: StepDetails,
    val application: StepDetails
)

data class StepDetails(
    val duration: String,
    val method: String,
    val content: String
)

enum class PreparationStatus {
    DRAFT, SUBMITTED, APPROVED, REJECTED, ARCHIVED
}

fun PreparationStatus.labelFr(): String = when (this) {
    PreparationStatus.DRAFT -> "Brouillon"
    PreparationStatus.SUBMITTED -> "En attente"
    PreparationStatus.APPROVED -> "Approuvée"
    PreparationStatus.REJECTED -> "Rejetée"
    PreparationStatus.ARCHIVED -> "Archivée"
}
