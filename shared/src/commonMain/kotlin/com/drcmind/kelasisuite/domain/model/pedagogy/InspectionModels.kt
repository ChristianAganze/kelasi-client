package com.drcmind.kelasisuite.domain.model.pedagogy

enum class InspectionRating(val label: String, val scorePercentage: Int, val colorHex: Long) {
    EXCELLENT("Excellent", 90, 0xFF2E7D32),
    TRES_BIEN("Très Bien", 80, 0xFF1976D2),
    BIEN("Bien / Satisfaisant", 65, 0xFF388E3C),
    A_AMELIORER("À Améliorer", 50, 0xFFF57C00),
    INSUFFISANT("Insuffisant / Critique", 30, 0xFFD32F2F)
}

data class InspectionCriteriaScore(
    val criteriaId: String,
    val title: String,
    val description: String,
    val maxScore: Int,
    val awardedScore: Int,
    val remarks: String = ""
)

data class ClassInspectionReport(
    val id: Long = 0L,
    val teacherId: Long,
    val teacherName: String,
    val classroomName: String,
    val subjectName: String,
    val lessonTopic: String,
    val inspectionDate: String,
    val inspectorName: String,
    val inspectorRole: String = "Préfet des Études",
    val globalScore: Int, // e.g. 85 / 100
    val rating: InspectionRating,
    val strengths: String,
    val areasForImprovement: String,
    val recommendations: String,
    val teacherFeedback: String = "",
    val isSignedByInspector: Boolean = true,
    val isAcknowledgedByTeacher: Boolean = false
)
