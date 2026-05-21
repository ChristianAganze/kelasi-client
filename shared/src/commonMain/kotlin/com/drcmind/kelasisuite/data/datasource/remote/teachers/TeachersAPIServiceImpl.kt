package com.drcmind.kelasisuite.data.datasource.remote.teachers

import com.drcmind.kelasisuite.domain.dto.HomeroomAssignmentDTO
import com.drcmind.kelasisuite.domain.dto.HomeroomAssignmentRequest
import com.drcmind.kelasisuite.domain.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.domain.dto.TeacherProfileRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class TeachersAPIServiceImpl(private val httpClient: HttpClient) : TeachersAPIService {

    override suspend fun createTeacher(creationRequest: TeacherProfileRequest): TeacherProfileDTO {
        return httpClient.post("teachers") {
            setBody(creationRequest)
        }.body()
    }

    override suspend fun updateTeacher(teacherId: Long, updateRequest: TeacherProfileRequest): TeacherProfileDTO {
        return httpClient.put("teachers/$teacherId") {
            setBody(updateRequest)
        }.body()
    }

    override suspend fun getTeachers(schoolId: Long): List<TeacherProfileDTO> {
        return httpClient.get("schools/$schoolId/teachers").body()
    }

    override suspend fun getTeacher(teacherId: Long): TeacherProfileDTO {
        return httpClient.get("teachers/$teacherId").body()
    }

    override suspend fun assignHomeroomTeacher(
        academicYearId: Long,
        request: HomeroomAssignmentRequest
    ): HomeroomAssignmentDTO {
        return httpClient.post("homeroom-assignments") {
            url {
                parameter("academicYearId", academicYearId)
            }
            setBody(request)
        }.body()
    }

    override suspend fun getHomeroomTeacher(
        classId: Long,
        academicYearId: Long
    ): HomeroomAssignmentDTO {
        return httpClient.get("classes/$classId/homeroom-teacher") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun getHomeroomAssignmentsByTeacher(
        teacherProfileId: Long,
        academicYearId: Long
    ): List<HomeroomAssignmentDTO> {
        return httpClient.get("teachers/$teacherProfileId/homeroom-assignments") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }
}