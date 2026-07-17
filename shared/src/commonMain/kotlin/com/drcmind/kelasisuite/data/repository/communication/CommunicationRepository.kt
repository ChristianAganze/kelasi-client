package com.drcmind.kelasisuite.data.repository.communication

import com.drcmind.kelasisuite.data.datasource.remote.dto.ConversationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.MessageDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CommunicationRepository {
    fun getConversations(userId: Long): Flow<Resource<List<ConversationDTO>>>
    fun getMessages(conversationId: Long): Flow<Resource<List<MessageDTO>>>
    fun sendMessage(message: MessageDTO): Flow<Resource<MessageDTO>>
}
