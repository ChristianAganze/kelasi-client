package com.drcmind.kelasisuite.data.datasource.remote.assignments

import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentRequest
import com.drcmind.kelasisuite.domain.dto.TemplateSubjectDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AssignmentAPIServiceImpl(private val httpClient: HttpClient) : AssignmentAPIService {
    override suspend fun getAssignmentsForClass(classId: Long, academicYearId: Long): List<TeachingAssignmentDTO> {
        return httpClient.get("classes/$classId/assignments") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun getPendingAssignmentsForClass(classId: Long, academicYearId: Long): List<TemplateSubjectDTO> {
        return httpClient.get("classes/$classId/pending-assignments") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun createTeachingAssignment(request: TeachingAssignmentRequest): TeachingAssignmentDTO {
        return httpClient.post("assignments") {
            setBody(request)
        }.body()
    }

    override suspend fun deleteTeachingAssignment(assignmentId: Long) {
        httpClient.delete("assignments/$assignmentId")
    }
}
