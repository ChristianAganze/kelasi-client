package com.drcmind.kelasisuite.data.datasource.remote.parent

import com.drcmind.kelasisuite.data.datasource.remote.dto.AttendanceDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ChildDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.FeeDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentDashboardDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.PaymentDTO

interface ParentApiService {
    suspend fun getDashboardData(parentId: Long): ParentDashboardDTO
    suspend fun getChildren(parentId: Long): List<ChildDTO>
    suspend fun getChildAttendance(childId: Long): List<AttendanceDTO>
    suspend fun getChildGrades(childId: Long): List<GradeDTO>
    suspend fun getFees(parentId: Long): List<FeeDTO>
    suspend fun getPayments(parentId: Long): List<PaymentDTO>
}
