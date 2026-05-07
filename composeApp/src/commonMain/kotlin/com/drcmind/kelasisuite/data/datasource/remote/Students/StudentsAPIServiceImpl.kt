package com.drcmind.kelasisuite.data.datasource.remote.Students

import com.drcmind.kelasisuite.domain.dto.GetStudentRequest
import com.drcmind.kelasisuite.domain.dto.GetStudentResponse
import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.dto.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class StudentsAPIServiceImpl(private val httpClient: HttpClient) : StudentsAPIService {


    override suspend fun getStudent(loginRequest: GetStudentRequest): GetStudentResponse {
        return httpClient.post("auth/login") {
            setBody(loginRequest)
        }.body()
    }
}