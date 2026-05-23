package com.drcmind.kelasisuite.data.datasource.remote.assignments

import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentRequest
import com.drcmind.kelasisuite.domain.dto.TemplateSubjectDTO

interface AssignmentAPIService {
    suspend fun getAssignmentsForClass(classId: Long, academicYearId: Long): List<TeachingAssignmentDTO>
    suspend fun getPendingAssignmentsForClass(classId: Long, academicYearId: Long): List<TemplateSubjectDTO>
    suspend fun createTeachingAssignment(request: TeachingAssignmentRequest): TeachingAssignmentDTO
    suspend fun deleteTeachingAssignment(assignmentId: Long)
}
