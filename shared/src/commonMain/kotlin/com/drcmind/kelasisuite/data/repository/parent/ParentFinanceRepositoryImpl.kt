package com.drcmind.kelasisuite.data.repository.parent

import com.drcmind.kelasisuite.data.datasource.remote.dto.FeeDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.PaymentDTO
import com.drcmind.kelasisuite.data.datasource.remote.parent.ParentApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ParentFinanceRepositoryImpl(
    private val parentApiService: ParentApiService
) : ParentFinanceRepository {
    override fun getFees(parentId: Long): Flow<Resource<List<FeeDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val data = parentApiService.getFees(parentId)
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erreur / Error fetching data"))
        }
    }

    override fun getPayments(parentId: Long): Flow<Resource<List<PaymentDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val data = parentApiService.getPayments(parentId)
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erreur / Error fetching data"))
        }
    }
}
