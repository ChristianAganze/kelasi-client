package com.drcmind.kelasisuite.data.repository.parent

import com.drcmind.kelasisuite.data.datasource.remote.dto.FeeDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.PaymentDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ParentFinanceRepository {
    fun getFees(parentId: Long): Flow<Resource<List<FeeDTO>>>
    fun getPayments(parentId: Long): Flow<Resource<List<PaymentDTO>>>
}
