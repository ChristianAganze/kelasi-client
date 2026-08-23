package com.drcmind.kelasisuite.data.datasource.remote.parent

import com.drcmind.kelasisuite.data.datasource.remote.dto.AttendanceDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ChildDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.FeeDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.NotificationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentDashboardDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.PaymentDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class ParentApiServiceImpl(
    private val httpClient: HttpClient
) : ParentApiService {

    override suspend fun getDashboardData(parentId: Long): ParentDashboardDTO {
        return httpClient.get("parent/dashboard/$parentId").body()
    }

    override suspend fun getChildren(parentId: Long): List<ChildDTO> {
        return httpClient.get("parent/children/$parentId").body()
    }

    override suspend fun getChildAttendance(childId: Long): List<AttendanceDTO> {
        return httpClient.get("parent/children/$childId/attendance").body()
    }

    override suspend fun getChildGrades(childId: Long): List<GradeDTO> {
        return httpClient.get("parent/children/$childId/grades").body()
    }

    override suspend fun getFees(parentId: Long): List<FeeDTO> {
        return httpClient.get("parent/finance/$parentId/fees").body()
    }

    override suspend fun getPayments(parentId: Long): List<PaymentDTO> {
        return httpClient.get("parent/finance/$parentId/payments").body()
    }
}
