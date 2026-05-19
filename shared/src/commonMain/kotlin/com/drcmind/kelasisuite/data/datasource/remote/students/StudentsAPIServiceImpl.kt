package com.drcmind.kelasisuite.data.datasource.remote.students

import com.drcmind.kelasisuite.domain.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*

class StudentsAPIServiceImpl(private val httpClient: HttpClient) : StudentsAPIService {
    override suspend fun createStudent(creationRequest: StudentCreationRequest): StudentDTO {
        return httpClient.post("students") {
            setBody(creationRequest)
        }.body()
    }

    override suspend fun updateStudent(
        studentId: Long,
        updateRequest: StudentCreationRequest
    ): StudentDTO {
        return httpClient.put("students/$studentId") {
            setBody(updateRequest)
        }.body()
    }

    override suspend fun getStudents(schoolId: Long): List<StudentDTO> {
        return httpClient.get("schools/$schoolId/students/all").body()
    }

    override suspend fun getEnrolledStudents(
        schoolId: Long,
        academicYearId: Long
    ): List<StudentDTO> {
        return httpClient.get("schools/$schoolId/enrolled-students") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun getStudentsForClass(
        classId: Long,
        academicYearId: Long
    ): List<StudentDTO> {
        return httpClient.get("classes/$classId/students") {
            url {
                parameter("academicYearId", academicYearId)
            }
        }.body()
    }

    override suspend fun getStudent(studentId: Long): StudentDTO {
        return httpClient.get("students/$studentId").body()
    }

    override suspend fun enrollStudent(request: EnrollmentRequest): StudentDTO {
        return httpClient.post("enrollments") {
            setBody(request)
        }.body()
    }

    override suspend fun updateEnrollment(
        enrollmentId: Long,
        request: UpdateEnrollmentRequest
    ): StudentDTO {
        return httpClient.put("enrollments/$enrollmentId") {
            setBody(request)
        }.body()
    }
}