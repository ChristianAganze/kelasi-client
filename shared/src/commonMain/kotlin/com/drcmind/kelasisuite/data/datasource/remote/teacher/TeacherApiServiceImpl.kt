package com.drcmind.kelasisuite.data.datasource.remote.teacher

import com.drcmind.kelasisuite.data.datasource.remote.dto.ClassLogDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.LessonPreparationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentEvaluationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ReportCardDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TeacherApiServiceImpl(
    private val httpClient: HttpClient
) : TeacherApiService {

    override suspend fun createPreparation(request: LessonPreparationDTO): LessonPreparationDTO {
        return httpClient.post("teacher/preparations") {
            setBody(request)
        }.body()
    }

    override suspend fun getPreparations(teachingAssignmentId: Long): List<LessonPreparationDTO> {
        return httpClient.get("teacher/preparations/assignment/$teachingAssignmentId").body()
    }

    override suspend fun submitPreparation(preparationId: Long): LessonPreparationDTO {
        return httpClient.put("teacher/preparations/$preparationId/submit").body()
    }

    override suspend fun createClassLog(request: ClassLogDTO): ClassLogDTO {
        return httpClient.post("teacher/class-logs") {
            setBody(request)
        }.body()
    }

    override suspend fun getClassLogs(teachingAssignmentId: Long): List<ClassLogDTO> {
        return httpClient.get("teacher/class-logs/assignment/$teachingAssignmentId").body()
    }

    override suspend fun submitStudentEvaluation(request: StudentEvaluationDTO): StudentEvaluationDTO {
        return httpClient.post("teacher/evaluations") {
            setBody(request)
        }.body()
    }

    override suspend fun getStudentEvaluations(teachingAssignmentId: Long): List<StudentEvaluationDTO> {
        return httpClient.get("teacher/evaluations/assignment/$teachingAssignmentId").body()
    }

    override suspend fun getReportCards(classId: Long, termId: Long): List<ReportCardDTO> {
        return httpClient.get("teacher/report-cards/class/$classId/term/$termId").body()
    }

    override suspend fun saveReportCard(request: ReportCardDTO): ReportCardDTO {
        return httpClient.post("teacher/report-cards") {
            setBody(request)
        }.body()
    }
}
