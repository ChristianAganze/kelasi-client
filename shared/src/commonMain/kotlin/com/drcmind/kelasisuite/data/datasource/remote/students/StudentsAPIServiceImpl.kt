package com.drcmind.kelasisuite.data.datasource.remote.students

import com.drcmind.kelasisuite.domain.dto.StudentCreationRequest
import com.drcmind.kelasisuite.domain.dto.StudentDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class StudentsAPIServiceImpl(private val httpClient: HttpClient) : StudentsAPIService {
    override suspend fun createStudent(creationRequest: StudentCreationRequest): StudentDTO {
        return httpClient.post("students") {
            setBody(creationRequest)
        }.body()
    }

    override suspend fun updateStudent(studentId: Long, updateRequest: StudentCreationRequest): StudentDTO {
        return httpClient.put("students/$studentId") {
            setBody(updateRequest)
        }.body()
    }

    override suspend fun getStudents(schoolId: Long): List<StudentDTO> {
        return httpClient.get("schools/$schoolId/students/all").body()
    }

    override suspend fun getStudent(studentId: Long): StudentDTO {
        return httpClient.get("students/$studentId").body()
    }
}