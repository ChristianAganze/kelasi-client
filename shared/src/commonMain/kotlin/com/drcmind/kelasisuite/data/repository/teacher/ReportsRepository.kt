package com.drcmind.kelasisuite.data.repository.teacher

import com.drcmind.kelasisuite.data.datasource.remote.dto.ReportCardDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {
    fun getReportCards(classId: Long, termId: Long): Flow<Resource<List<ReportCardDTO>>>
    fun saveReportCard(request: ReportCardDTO): Flow<Resource<ReportCardDTO>>
}
