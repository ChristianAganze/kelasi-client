package com.drcmind.kelasisuite.data.repository.parent

import com.drcmind.kelasisuite.data.datasource.remote.dto.AttendanceDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ChildDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ParentChildrenRepository {
    fun getChildren(parentId: Long): Flow<Resource<List<ChildDTO>>>
    fun getChildAttendance(childId: Long): Flow<Resource<List<AttendanceDTO>>>
    fun getChildGrades(childId: Long): Flow<Resource<List<GradeDTO>>>
}
