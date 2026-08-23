package com.drcmind.kelasisuite.data.repository.parent

import com.drcmind.kelasisuite.data.datasource.remote.dto.AttendanceDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ChildDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeDTO
import com.drcmind.kelasisuite.data.datasource.remote.parent.ParentApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ParentChildrenRepositoryImpl(
    private val parentApiService: ParentApiService
) : ParentChildrenRepository {
    override fun getChildren(parentId: Long): Flow<Resource<List<ChildDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val data = parentApiService.getChildren(parentId)
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erreur / Error fetching data"))
        }
    }

    override fun getChildAttendance(childId: Long): Flow<Resource<List<AttendanceDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val data = parentApiService.getChildAttendance(childId)
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erreur / Error fetching data"))
        }
    }

    override fun getChildGrades(childId: Long): Flow<Resource<List<GradeDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val data = parentApiService.getChildGrades(childId)
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erreur / Error fetching data"))
        }
    }
}
