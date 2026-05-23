package com.drcmind.kelasisuite.data.repository.assignments

import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentRequest
import com.drcmind.kelasisuite.domain.dto.TemplateSubjectDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AssignmentRepository {
    fun getAssignmentsForClass(classId: Long): Flow<Resource<List<TeachingAssignmentDTO>>>
    fun getPendingAssignmentsForClass(classId: Long): Flow<Resource<List<TemplateSubjectDTO>>>
    fun createTeachingAssignment(request: TeachingAssignmentRequest): Flow<Resource<TeachingAssignmentDTO>>
    fun deleteTeachingAssignment(assignmentId: Long): Flow<Resource<Unit>>
}
