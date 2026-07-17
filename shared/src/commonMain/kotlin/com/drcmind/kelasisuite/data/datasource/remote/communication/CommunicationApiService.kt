package com.drcmind.kelasisuite.data.datasource.remote.communication

import com.drcmind.kelasisuite.data.datasource.remote.dto.ConversationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.MessageDTO

interface CommunicationApiService {
    suspend fun getConversations(userId: Long): List<ConversationDTO>
    suspend fun getMessages(conversationId: Long): List<MessageDTO>
    suspend fun sendMessage(message: MessageDTO): MessageDTO
}
