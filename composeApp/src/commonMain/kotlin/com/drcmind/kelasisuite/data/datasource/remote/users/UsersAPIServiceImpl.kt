package com.drcmind.kelasisuite.data.datasource.remote.users

import com.drcmind.kelasisuite.domain.dto.UserDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class UsersAPIServiceImpl(private val httpClient: HttpClient) : UsersAPIService {
    override suspend fun getUserBySchoolId(schoolId: Long): List<UserDTO> {
        return httpClient.get("schools/$schoolId/users").body()
    }

}