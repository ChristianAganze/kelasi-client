package com.drcmind.kelasisuite.data.repository.parents

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateParentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentStudentLinkageDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentStudentLinkageRequest
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ParentsRepositoryImp(
    private val schoolAdminService: SchoolAdminApiService,
    private val settingsStorage: SettingsStorage
) : ParentsRepository  {
    override fun createParent(createParentRequest: CreateParentRequest): Flow<Resource<ParentDto>> {
        return flow {
            emit(Resource.Loading())
            val result = schoolAdminService.createParent(createParentRequest)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown Error"))
        }
    }

    override fun linkStudentToParent(parentStudentLinkageRequest: ParentStudentLinkageRequest): Flow<Resource<ParentStudentLinkageDto>> {
        return flow {
            emit(Resource.Loading())
            val result = schoolAdminService.linkStudentToParent(parentStudentLinkageRequest)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown Error"))
        }
    }

    override fun unlinkStudentFromParent(linkageId: Long): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            val result = schoolAdminService.unlinkStudentFromParent(linkageId)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown Error"))
        }
    }

    override fun updateParent(
        parentId: Long,
        createParentRequest: CreateParentRequest
    ): Flow<Resource<ParentDto>> {
        return flow {
            emit(Resource.Loading())
            val result = schoolAdminService.updateParent(parentId, createParentRequest)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown Error"))
        }
    }

    override fun deleteParent(parentId: Long): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            val result = schoolAdminService.deleteParent(parentId)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown Error"))
        }
    }

    override fun getParentById(parentId: Long): Flow<Resource<ParentDto>> {
        return flow {
            emit(Resource.Loading())
            val result = schoolAdminService.getParentById(parentId)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown Error"))
        }
    }

    override fun getParentsBySchool(): Flow<Resource<List<ParentDto>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getSchool()?.id
            val result = schoolAdminService.getParentsBySchool(schoolId!!)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown Error"))
        }
    }

}