package com.drcmind.kelasisuite.data.repository.schools

import com.drcmind.kelasisuite.data.datasource.remote.schools.SchoolsAPIService
import com.drcmind.kelasisuite.domain.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SchoolRepositoryImpl(
    private val apiService: SchoolsAPIService
) : SchoolRepository {

    override fun getSchool(schoolId: Long): Flow<Resource<SchoolDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getSchool(schoolId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getSchoolSections(schoolId: Long): Flow<Resource<List<SchoolSectionDTO>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getSchoolSections(schoolId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getClasses(schoolId: Long): Flow<Resource<List<SchoolClassDTO>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getClasses(schoolId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun createClass(request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.createClass(request)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.updateClass(classId, request)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun deleteClass(classId: Long): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            apiService.deleteClass(classId)
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}