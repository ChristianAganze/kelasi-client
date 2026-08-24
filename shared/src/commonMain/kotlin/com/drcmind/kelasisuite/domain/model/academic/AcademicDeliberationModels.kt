package com.drcmind.kelasisuite.domain.model.academic

import kotlinx.serialization.Serializable

enum class AcademicPeriodStatus {
    OPEN,         // Saisie ouverte aux enseignants
    DELIBERATING, // En cours de délibération par la direction
    CLOSED        // Clôturée & Bulletins officialisés
}

enum class AcademicDecision(val label: String, val shortCode: String) {
    ADMITTED("Admis(e)", "ADM"),
    ADMITTED_WITH_WARNING("Admis(e) avec avertissement", "ADA"),
    CONDITIONAL("Admis(e) sous condition", "ADC"),
    RETAKE("Ajourné(e) / À reprendre", "AJR"),
    EXPELLED("Exclu(e)", "EXC")
}

@Serializable
data class StudentPalmaresItem(
    val studentId: Long,
    val studentName: String,
    val rollNumber: String,
    val totalObtained: Double,
    val totalMax: Double,
    val percentage: Double,
    val rank: Int,
    val conductLabel: String,
    val applicationPercentage: Double,
    val decision: AcademicDecision,
    val isDeliberated: Boolean = true
)

@Serializable
data class ClassPalmaresSummary(
    val classId: Long,
    val className: String,
    val periodLabel: String,
    val schoolYear: String,
    val totalStudents: Int,
    val classAveragePercentage: Double,
    val highestPercentage: Double,
    val lowestPercentage: Double,
    val passRatePercentage: Double,
    val students: List<StudentPalmaresItem>
)
