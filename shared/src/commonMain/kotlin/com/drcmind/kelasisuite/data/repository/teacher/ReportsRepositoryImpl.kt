package com.drcmind.kelasisuite.data.repository.teacher

import com.drcmind.kelasisuite.data.datasource.remote.dto.ReportCardDTO
import com.drcmind.kelasisuite.data.datasource.remote.teacher.TeacherApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReportsRepositoryImpl(
    private val teacherApiService: TeacherApiService
) : ReportsRepository {
    override fun getReportCards(classId: Long, termId: Long): Flow<Resource<List<ReportCardDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val reportCards = teacherApiService.getReportCards(classId, termId)
            emit(Resource.Success(reportCards))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erreur / Error"))
        }
    }

    override fun saveReportCard(request: ReportCardDTO): Flow<Resource<ReportCardDTO>> = flow {
        emit(Resource.Loading())
        try {
            val reportCard = teacherApiService.saveReportCard(request)
            emit(Resource.Success(reportCard))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erreur / Error"))
        }
    }
}
