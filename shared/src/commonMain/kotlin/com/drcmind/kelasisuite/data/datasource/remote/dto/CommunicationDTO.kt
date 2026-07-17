package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDTO(
    val id: Long? = null,
    val senderId: Long,
    val receiverId: Long,
    val content: String,
    val timestamp: String,
    val isRead: Boolean = false
)

@Serializable
data class ConversationDTO(
    val id: Long,
    val participants: List<Long>,
    val participantNames: Map<Long, String>, // To easily display the names
    val lastMessage: MessageDTO?,
    val unreadCount: Int = 0
)
