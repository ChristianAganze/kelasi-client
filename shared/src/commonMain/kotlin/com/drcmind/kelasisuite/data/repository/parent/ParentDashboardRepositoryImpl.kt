package com.drcmind.kelasisuite.data.repository.parent

import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentDashboardDTO
import com.drcmind.kelasisuite.data.datasource.remote.parent.ParentApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ParentDashboardRepositoryImpl(
    private val parentApiService: ParentApiService
) : ParentDashboardRepository {
    override fun getDashboardData(parentId: Long): Flow<Resource<ParentDashboardDTO>> = flow {
        emit(Resource.Loading())
        try {
            val dashboardData = parentApiService.getDashboardData(parentId)
            emit(Resource.Success(dashboardData))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erreur lors de la récupération des données / Error fetching data"))
        }
    }
}
