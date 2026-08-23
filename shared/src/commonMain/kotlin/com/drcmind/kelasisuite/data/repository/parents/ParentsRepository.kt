package com.drcmind.kelasisuite.data.repository.parents

import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateParentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentStudentLinkageDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentStudentLinkageRequest
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface  ParentsRepository {
    fun createParent(createParentRequest: CreateParentRequest) : Flow<Resource<ParentDto>>
    fun linkStudentToParent(parentStudentLinkageRequest: ParentStudentLinkageRequest) : Flow<Resource<ParentStudentLinkageDto>>
    fun unlinkStudentFromParent(linkageId: Long) : Flow<Resource<Unit>>
    fun updateParent(parentId : Long, createParentRequest: CreateParentRequest) : Flow<Resource<ParentDto>>
    fun deleteParent(parentId : Long) : Flow<Resource<Unit>>
    fun getParentById(parentId: Long) : Flow<Resource<ParentDto>>
    fun getParentsBySchool() : Flow<Resource<List<ParentDto>>>
}