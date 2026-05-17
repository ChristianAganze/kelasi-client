package com.drcmind.kelasisuite.data.datasource.remote.profile

import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.dto.UserDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ProfileAPIServiceImpl(private val httpClient: HttpClient) : ProfileAPIService {

    override suspend fun getUser(userId: Long): UserDTO {
        return httpClient.get("users/$userId").body()
    }

    override suspend fun getSchool(schoolId: Long): SchoolDTO {
        return httpClient.get("schools/$schoolId").body()
    }
}