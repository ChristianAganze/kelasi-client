package com.drcmind.kelasisuite.data.datasource.remote.schools

import com.drcmind.kelasisuite.domain.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class SchoolsAPIServiceImpl(private val httpClient: HttpClient) : SchoolsAPIService {

    override suspend fun getSchool(schoolId: Long): SchoolDTO {
        return httpClient.get("schools/$schoolId").body()
    }

    override suspend fun getSchoolSections(schoolId: Long): List<SchoolSectionDTO> {
        return httpClient.get("schools/$schoolId/school-sections").body()
    }

    override suspend fun getSectionBySchoolSectionAndSchool(
        schoolSectionId: Long,
        schoolId: Long
    ): List<SectionDTO> {
        return httpClient.get("schools/$schoolId/school-sections/$schoolSectionId/sections").body()
    }

    override suspend fun getOfferedMajorBySchoolAndBySection(
        schoolId: Long,
        sectionId: Long
    ): List<MajorDto> {
        return httpClient.get("schools/$schoolId/sections/$sectionId/majors").body()
    }

    override suspend fun getOfferedMajorsForSchool(schoolId: Long): List<MajorDto> {
        return httpClient.get("schools/$schoolId/majors").body()
    }

    override suspend fun getGradeLevelsBySchoolAndByMajor(
        schoolId: Long,
        majorId: Long
    ): List<GradeLevelDTO> {
        return httpClient.get("schools/$schoolId/majors/$majorId/grade-levels").body()
    }

    override suspend fun getClassesForSchool(schoolId: Long): List<SchoolClassDTO> {
        return httpClient.get("schools/$schoolId/classes").body()
    }

    override suspend fun getClassesForSchoolAndMajor(schoolId: Long, majorId: Long): List<SchoolClassDTO> {
        return httpClient.get("schools/$schoolId/majors/$majorId/classes").body()
    }

    override suspend fun getClassesBySchoolAndGradeLevel(
        schoolId: Long,
        gradeLevelId: Long
    ): List<SchoolClassDTO> {
        return httpClient.get("schools/$schoolId/grade-levels/$gradeLevelId/classes").body()
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

    override suspend fun getAcademicYears(): List<AcademicYearDTO> {
        return httpClient.get("templates/academic-years").body()
    }

    override suspend fun getEvaluationPeriodsBySchool(schoolId: Long): List<EvaluationPeriodBySchoolDTO> {
        return httpClient.get("schools/$schoolId/evaluation-periods").body()
    }
}