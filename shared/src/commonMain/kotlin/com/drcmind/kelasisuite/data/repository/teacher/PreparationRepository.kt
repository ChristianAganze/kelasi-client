package com.drcmind.kelasisuite.data.repository.teacher

import com.drcmind.kelasisuite.data.datasource.remote.dto.LessonPreparationDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface PreparationRepository {
    fun createPreparation(request: LessonPreparationDTO): Flow<Resource<LessonPreparationDTO>>
    fun getPreparations(teachingAssignmentId: Long): Flow<Resource<List<LessonPreparationDTO>>>
    fun submitPreparation(preparationId: Long): Flow<Resource<LessonPreparationDTO>>
}
