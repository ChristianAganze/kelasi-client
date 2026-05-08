package com.drcmind.kelasisuite.data.datasource.remote.schools

import com.drcmind.kelasisuite.domain.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.dto.SchoolSectionDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class SchoolsAPIServiceImpl(private val httpClient: HttpClient) : SchoolsAPIService {

    override suspend fun getSchool(schoolId: Long): SchoolDTO {
        return httpClient.get("schools/$schoolId").body()
    }

    override suspend fun getSchoolSections(schoolId: Long): List<SchoolSectionDTO> {
        return httpClient.get("schools/$schoolId/school-sections").body()
    }

    override suspend fun getClasses(schoolId: Long): List<SchoolClassDTO> {
        return httpClient.get("schools/$schoolId/classes").body()
    }

    override suspend fun createClass(request: CreateClassFromTemplateRequest): SchoolClassDTO {
        return httpClient.post("schools/classes/from-template") {
            setBody(request)
        }.body()
    }

    override suspend fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): SchoolClassDTO {
        return httpClient.put("schools/classes/$classId") {
            setBody(request)
        }.body()
    }

    override suspend fun deleteClass(classId: Long) {
        httpClient.delete("schools/classes/$classId")
    }
}