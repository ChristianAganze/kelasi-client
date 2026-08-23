package com.drcmind.kelasisuite.data.repository.parent

import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentDashboardDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ParentDashboardRepository {
    fun getDashboardData(parentId: Long): Flow<Resource<ParentDashboardDTO>>
}
