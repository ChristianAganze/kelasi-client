package com.drcmind.kelasisuite.data.datasource.remote.schools

import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SchoolsAPIServiceImpl(private val httpClient: HttpClient) : SchoolsAPIService {

    override suspend fun getSchool(schoolId: Long): SchoolDTO {
        return httpClient.get("schools/$schoolId").body()
    }
}