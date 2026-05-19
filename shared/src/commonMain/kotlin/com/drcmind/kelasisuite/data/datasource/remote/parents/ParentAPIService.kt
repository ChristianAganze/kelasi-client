package com.drcmind.kelasisuite.data.datasource.remote.parents

import com.drcmind.kelasisuite.domain.dto.CreateParentRequest
import com.drcmind.kelasisuite.domain.dto.ParentDto
import com.drcmind.kelasisuite.domain.dto.ParentStudentLinkageDto
import com.drcmind.kelasisuite.domain.dto.ParentStudentLinkageRequest

interface ParentAPIService {
    suspend fun getParentsBySchool(schoolId : Long) : List<ParentDto>
    suspend fun createParent(request : CreateParentRequest) : ParentDto
    suspend fun linkStudentToParent(linkageRequest: ParentStudentLinkageRequest) : ParentStudentLinkageDto
    suspend fun unlinkStudentFromParent(linkageId: Long)
    suspend fun updateParent(parentId : Long, createParentRequest: CreateParentRequest) : ParentDto
    suspend fun deleteParent(parentId : Long)
    suspend fun getParentById(parentId: Long) : ParentDto

}