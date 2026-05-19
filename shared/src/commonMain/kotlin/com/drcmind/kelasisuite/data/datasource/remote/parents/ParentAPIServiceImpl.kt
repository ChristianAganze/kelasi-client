package com.drcmind.kelasisuite.data.datasource.remote.parents

import com.drcmind.kelasisuite.domain.dto.CreateParentRequest
import com.drcmind.kelasisuite.domain.dto.ParentDto
import com.drcmind.kelasisuite.domain.dto.ParentStudentLinkageDto
import com.drcmind.kelasisuite.domain.dto.ParentStudentLinkageRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class ParentAPIServiceImpl(private val httpClient: HttpClient) : ParentAPIService {
    override suspend fun getParentsBySchool(schoolId: Long): List<ParentDto> {
        return httpClient.get("parents/schools/$schoolId").body()
    }

    override suspend fun createParent(request: CreateParentRequest): ParentDto {
        return httpClient.post("parents"){
            setBody(request)
        }.body()
    }

    override suspend fun linkStudentToParent(linkageRequest: ParentStudentLinkageRequest): ParentStudentLinkageDto {
        return httpClient.post("parents/link-student"){
            setBody(linkageRequest)
        } .body()
    }

    override suspend fun unlinkStudentFromParent(linkageId: Long) {
        return httpClient.delete ("parents/unlink-student/$linkageId").body()
    }

    override suspend fun updateParent(
        parentId: Long,
        createParentRequest: CreateParentRequest
    ): ParentDto {
        return httpClient.put("parents/$parentId"){
            setBody(createParentRequest)
        } .body()
    }

    override suspend fun deleteParent(parentId: Long) {
        return httpClient.get("parents/$parentId").body()
    }

    override suspend fun getParentById(parentId: Long): ParentDto {
        return httpClient.get("parents/$parentId").body()
    }
}