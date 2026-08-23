package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ParentDashboardDTO(
    val parentId: Long,
    val totalChildren: Int,
    val unreadMessages: Int,
    val pendingFees: Double,
    val recentNotifications: List<NotificationDTO>
)

@Serializable
data class NotificationDTO(
    val id: Long,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean
)
