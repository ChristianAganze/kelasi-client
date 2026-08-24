package com.drcmind.kelasisuite.data.repository.finance

import com.drcmind.kelasisuite.domain.model.finance.*
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SchoolFinanceRepository {
    fun getFinanceDashboard(schoolId: Long): Flow<Resource<SchoolFinanceDashboardSummary>>
    fun getFeeStructures(schoolId: Long): Flow<Resource<List<FeeStructureItem>>>
    suspend fun saveFeeStructure(fee: FeeStructureItem): Resource<FeeStructureItem>
    fun getRecentTransactions(schoolId: Long): Flow<Resource<List<PaymentTransaction>>>
    suspend fun recordPayment(transaction: PaymentTransaction): Resource<PaymentTransaction>
    fun getStudentsSolvency(classId: Long?): Flow<Resource<List<StudentSolvencyItem>>>
}
