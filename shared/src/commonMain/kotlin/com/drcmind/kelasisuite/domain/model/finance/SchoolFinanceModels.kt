package com.drcmind.kelasisuite.domain.model.finance

import kotlinx.serialization.Serializable

enum class FeeCategory(val label: String) {
    TUITION("Minerval / Frais Scolaires"),
    STATE_EXAM("Frais d'Examen d'État & TENAFEP"),
    REGISTRATION("Frais d'Inscription / Réinscription"),
    CANTEEN_TRANSPORT("Cantine & Transport"),
    UNIFORM_BADGE("Uniforme, Badge & Manuels"),
    OTHER("Autres Frais Annexes")
}

enum class PaymentMethod(val label: String, val shortCode: String) {
    CASH("Espèces / Caisse Directe", "CASH"),
    MPESA("Vodacom M-Pesa", "MPESA"),
    ORANGE_MONEY("Orange Money", "OM"),
    AIRTEL_MONEY("Airtel Money", "AM"),
    BANK_TRANSFER("Virement / Dépôt Bancaire", "BANK")
}

enum class SolvencyFilter(val label: String) {
    ALL("Tous les élèves"),
    SOLVENT("En règle (100% payé)"),
    PARTIAL("Acompte versé"),
    INSOLVENT("Non en règle (0% payé)")
}

@Serializable
data class FeeStructureItem(
    val id: Long = 0L,
    val title: String,
    val category: FeeCategory,
    val amount: Double,
    val currency: String = "USD",
    val targetClassId: Long? = null,
    val targetClassName: String = "Toutes les classes",
    val dueDate: String,
    val schoolYear: String = "2025-2026"
)

@Serializable
data class PaymentTransaction(
    val id: Long = 0L,
    val transactionRef: String,
    val studentId: Long,
    val studentName: String,
    val classroomName: String,
    val feeItemId: Long,
    val feeDescription: String,
    val amountPaid: Double,
    val currency: String = "USD",
    val paymentMethod: PaymentMethod,
    val paymentDate: String,
    val receivedBy: String,
    val notes: String? = null
)

@Serializable
data class StudentSolvencyItem(
    val studentId: Long,
    val studentName: String,
    val rollNumber: String,
    val classroomName: String,
    val parentPhone: String,
    val totalDue: Double,
    val totalPaid: Double,
    val balanceRemaining: Double,
    val paidPercentage: Double,
    val isFullyPaid: Boolean
)

@Serializable
data class SchoolFinanceDashboardSummary(
    val totalExpected: Double,
    val totalCollected: Double,
    val totalOutstanding: Double,
    val collectionRatePercentage: Double,
    val todayCollected: Double,
    val cashCollected: Double,
    val mobileMoneyCollected: Double,
    val bankCollected: Double,
    val totalStudentsCount: Int,
    val fullyPaidStudentsCount: Int,
    val partialPaidStudentsCount: Int,
    val nonPaidStudentsCount: Int
)
