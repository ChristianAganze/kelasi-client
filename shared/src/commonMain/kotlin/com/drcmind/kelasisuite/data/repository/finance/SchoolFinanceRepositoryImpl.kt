package com.drcmind.kelasisuite.data.repository.finance

import com.drcmind.kelasisuite.domain.model.finance.*
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SchoolFinanceRepositoryImpl : SchoolFinanceRepository {

    private val sampleFees = mutableListOf(
        FeeStructureItem(1L, "Minerval 1er Trimestre", FeeCategory.TUITION, 150.0, "USD", null, "Toutes les classes", "2025-10-15"),
        FeeStructureItem(2L, "Minerval 2ème Trimestre", FeeCategory.TUITION, 150.0, "USD", null, "Toutes les classes", "2026-01-20"),
        FeeStructureItem(3L, "Minerval 3ème Trimestre", FeeCategory.TUITION, 150.0, "USD", null, "Toutes les classes", "2026-04-15"),
        FeeStructureItem(4L, "Frais de Participation aux Examens d'État", FeeCategory.STATE_EXAM, 45.0, "USD", null, "Classes Terminales (6èmes)", "2026-05-10"),
        FeeStructureItem(5L, "Uniforme complet & Badge officiel", FeeCategory.UNIFORM_BADGE, 30.0, "USD", null, "Nouveaux inscrits", "2025-09-05")
    )

    private val sampleTransactions = mutableListOf(
        PaymentTransaction(101L, "REC-2026-0081", 1L, "Kabila Marc", "6ème Math-Physique A", 1L, "Minerval 1er Trimestre", 150.0, "USD", PaymentMethod.MPESA, "2026-01-14 09:30", "Comptable M. Joseph", "Paiement direct M-Pesa"),
        PaymentTransaction(102L, "REC-2026-0082", 2L, "Mbemba Sarah", "6ème Math-Physique A", 1L, "Minerval 1er Trimestre", 150.0, "USD", PaymentMethod.CASH, "2026-01-14 10:15", "Comptable M. Joseph", "Reçu espèces"),
        PaymentTransaction(103L, "REC-2026-0083", 3L, "Lumumba David", "6ème Math-Physique A", 1L, "Minerval 1er Trimestre (Acompte)", 75.0, "USD", PaymentMethod.ORANGE_MONEY, "2026-01-14 11:00", "Comptable M. Joseph", "Reste 75 USD à solder"),
        PaymentTransaction(104L, "REC-2026-0084", 4L, "Tshisekedi Grace", "6ème Math-Physique A", 1L, "Minerval 1er Trimestre", 150.0, "USD", PaymentMethod.BANK_TRANSFER, "2026-01-14 11:45", "Comptable M. Joseph", "Bordereau Rawbank")
    )

    private val sampleSolvency = mutableListOf(
        StudentSolvencyItem(1L, "Kabila Marc", "N° 01", "6ème Math-Physique A", "+243 812 345 678", 450.0, 450.0, 0.0, 100.0, true),
        StudentSolvencyItem(2L, "Mbemba Sarah", "N° 02", "6ème Math-Physique A", "+243 998 765 432", 450.0, 300.0, 150.0, 66.7, false),
        StudentSolvencyItem(3L, "Lumumba David", "N° 03", "6ème Math-Physique A", "+243 823 456 789", 450.0, 150.0, 300.0, 33.3, false),
        StudentSolvencyItem(4L, "Tshisekedi Grace", "N° 04", "6ème Math-Physique A", "+243 854 321 098", 450.0, 450.0, 0.0, 100.0, true),
        StudentSolvencyItem(5L, "Mukendi Alain", "N° 05", "6ème Math-Physique A", "+243 891 234 567", 450.0, 0.0, 450.0, 0.0, false),
        StudentSolvencyItem(6L, "Kalonji Esther", "N° 06", "6ème Math-Physique A", "+243 971 122 334", 450.0, 300.0, 150.0, 66.7, false),
        StudentSolvencyItem(7L, "Mbuyi Joel", "N° 07", "6ème Math-Physique A", "+243 819 876 543", 450.0, 450.0, 0.0, 100.0, true)
    )

    override fun getFinanceDashboard(schoolId: Long): Flow<Resource<SchoolFinanceDashboardSummary>> = flow {
        emit(Resource.Loading())
        val totalExpected = sampleSolvency.sumOf { it.totalDue }
        val totalCollected = sampleSolvency.sumOf { it.totalPaid }
        val totalOutstanding = sampleSolvency.sumOf { it.balanceRemaining }
        val rate = if (totalExpected > 0) (totalCollected / totalExpected) * 100.0 else 0.0

        val fullyPaid = sampleSolvency.count { it.isFullyPaid }
        val partialPaid = sampleSolvency.count { it.totalPaid > 0 && !it.isFullyPaid }
        val nonPaid = sampleSolvency.count { it.totalPaid == 0.0 }

        val summary = SchoolFinanceDashboardSummary(
            totalExpected = totalExpected,
            totalCollected = totalCollected,
            totalOutstanding = totalOutstanding,
            collectionRatePercentage = rate,
            todayCollected = sampleTransactions.sumOf { it.amountPaid },
            cashCollected = sampleTransactions.filter { it.paymentMethod == PaymentMethod.CASH }.sumOf { it.amountPaid },
            mobileMoneyCollected = sampleTransactions.filter { it.paymentMethod == PaymentMethod.MPESA || it.paymentMethod == PaymentMethod.ORANGE_MONEY || it.paymentMethod == PaymentMethod.AIRTEL_MONEY }.sumOf { it.amountPaid },
            bankCollected = sampleTransactions.filter { it.paymentMethod == PaymentMethod.BANK_TRANSFER }.sumOf { it.amountPaid },
            totalStudentsCount = sampleSolvency.size,
            fullyPaidStudentsCount = fullyPaid,
            partialPaidStudentsCount = partialPaid,
            nonPaidStudentsCount = nonPaid
        )
        emit(Resource.Success(summary))
    }

    override fun getFeeStructures(schoolId: Long): Flow<Resource<List<FeeStructureItem>>> = flow {
        emit(Resource.Loading())
        emit(Resource.Success(sampleFees))
    }

    override suspend fun saveFeeStructure(fee: FeeStructureItem): Resource<FeeStructureItem> {
        val newFee = if (fee.id == 0L) fee.copy(id = (sampleFees.maxOfOrNull { it.id } ?: 0L) + 1L) else fee
        sampleFees.removeAll { it.id == newFee.id }
        sampleFees.add(newFee)
        return Resource.Success(newFee)
    }

    override fun getRecentTransactions(schoolId: Long): Flow<Resource<List<PaymentTransaction>>> = flow {
        emit(Resource.Loading())
        emit(Resource.Success(sampleTransactions.sortedByDescending { it.id }))
    }

    override suspend fun recordPayment(transaction: PaymentTransaction): Resource<PaymentTransaction> {
        val nextId = (sampleTransactions.maxOfOrNull { it.id } ?: 100L) + 1L
        val ref = "REC-2026-00" + nextId
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateStr = "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')} ${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"

        val recorded = transaction.copy(
            id = nextId,
            transactionRef = ref,
            paymentDate = dateStr
        )
        sampleTransactions.add(0, recorded)

        // Update solvency
        val studentIndex = sampleSolvency.indexOfFirst { it.studentId == recorded.studentId }
        if (studentIndex != -1) {
            val current = sampleSolvency[studentIndex]
            val newPaid = (current.totalPaid + recorded.amountPaid).coerceAtMost(current.totalDue)
            val newRemaining = current.totalDue - newPaid
            val newPct = (newPaid / current.totalDue) * 100.0
            sampleSolvency[studentIndex] = current.copy(
                totalPaid = newPaid,
                balanceRemaining = newRemaining,
                paidPercentage = newPct,
                isFullyPaid = newRemaining <= 0.0
            )
        }

        return Resource.Success(recorded)
    }

    override fun getStudentsSolvency(classId: Long?): Flow<Resource<List<StudentSolvencyItem>>> = flow {
        emit(Resource.Loading())
        emit(Resource.Success(sampleSolvency))
    }
}
