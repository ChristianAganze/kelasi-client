package com.drcmind.kelasisuite.domain.model.parent

import com.drcmind.kelasisuite.domain.model.common.ElectronicSignature

data class CourseGradeItem(
    val courseName: String,
    val teacherName: String,
    val maxPoints: Double,
    val p1: Double?,
    val p2: Double?,
    val exam1: Double?,
    val p3: Double?,
    val p4: Double?,
    val exam2: Double?,
    val appreciation: String
) {
    val sem1Total: Double?
        get() = if (p1 != null && p2 != null && exam1 != null) p1 + p2 + exam1 else null

    val sem2Total: Double?
        get() = if (p3 != null && p4 != null && exam2 != null) p3 + p4 + exam2 else null

    val annualTotal: Double?
        get() = if (sem1Total != null && sem2Total != null) sem1Total!! + sem2Total!! else sem1Total

    val percentage: Double?
        get() = annualTotal?.let { (it / (maxPoints * 4)) * 100 }
}

data class ChildBulletin(
    val childId: Long,
    val childName: String,
    val className: String,
    val academicYear: String = "2025 - 2026",
    val section: String = "Scientifique / Biologie-Chimie",
    val rank: String = "3ème sur 42 élèves",
    val generalPercentage: Double = 78.4,
    val conductGrade: String = "Très Bonne (TB)",
    val totalAbsences: Int = 2,
    val principalRemark: String = "Excellent travail d'ensemble. Élève assidu et très motivé. Félicitations du conseil !",
    val courses: List<CourseGradeItem> = emptyList()
)

data class HomeworkItem(
    val id: String,
    val childId: Long,
    val subject: String,
    val teacherName: String,
    val title: String,
    val description: String,
    val assignedDate: String,
    val dueDate: String,
    val isCompleted: Boolean = false,
    val estimatedMinutes: Int = 45
)

data class ChildAttendanceLog(
    val id: String,
    val childId: Long,
    val date: String,
    val period: String, // e.g. "08h00 - 09h40"
    val subject: String,
    val status: String, // "Présent", "Retard", "Absent Justifié", "Absent Non Justifié"
    val remark: String = "",
    val justification: AbsenceJustification? = null
)

data class AbsenceJustification(
    val id: String,
    val childId: Long,
    val childName: String,
    val absenceDate: String,
    val reasonCategory: String, // "Maladie", "Raison familiale", "Consultation médicale", "Autre"
    val explanation: String,
    val parentName: String,
    val parentPhone: String,
    val submittedAt: String,
    val status: String = "Approuvé par la Direction", // "En attente", "Approuvé", "Rejeté"
    val electronicSignature: ElectronicSignature? = null
)

data class TeacherContact(
    val teacherName: String,
    val subject: String,
    val email: String,
    val phone: String,
    val officeHours: String
)

data class MobileMoneyProvider(
    val name: String,
    val code: String, // "MPESA", "ORANGE", "AIRTEL", "BANK"
    val colorHex: Long,
    val dialCode: String
)

data class PaymentReceipt(
    val receiptNumber: String,
    val transactionRef: String,
    val feeDescription: String,
    val studentName: String,
    val studentClass: String,
    val parentName: String,
    val amountPaid: Double,
    val currency: String = "USD",
    val paymentProvider: String, // "M-Pesa (Vodacom)", "Orange Money", "Airtel Money", "Banque Rawbank"
    val payerPhoneOrAccount: String,
    val paymentDate: String,
    val cashierName: String = "Guichet Électronique KelasiSuite",
    val verificationToken: String,
    val status: String = "Validé / Payé"
)
