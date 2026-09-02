package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDTO(
    val id: Long? = null,
    val senderId: Long,
    val receiverId: Long,
    val content: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val attachmentUrl: String? = null,
    val attachmentName: String? = null,
    val attachmentType: String? = null // e.g. "bulletin", "discipline", "attendance", "document"
)

@Serializable
data class ConversationDTO(
    val id: Long,
    val participants: List<Long>,
    val participantNames: Map<Long, String>,
    val lastMessage: MessageDTO?,
    val unreadCount: Int = 0,
    val participantRoles: Map<Long, String> = emptyMap(), // e.g. "Parent de Kabila Marc (6ème A)", "Direction des Études"
    val participantOnlineStatus: Map<Long, Boolean> = emptyMap(),
    val category: String = "PARENTS" // "PARENTS", "ADMIN", "TEACHERS"
)
