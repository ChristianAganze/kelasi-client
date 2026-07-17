package com.drcmind.kelasisuite.data.repository.communication

import com.drcmind.kelasisuite.data.datasource.remote.communication.CommunicationApiService
import com.drcmind.kelasisuite.data.datasource.remote.dto.ConversationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.MessageDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CommunicationRepositoryImpl(
    private val communicationApiService: CommunicationApiService
) : CommunicationRepository {

    override fun getConversations(userId: Long): Flow<Resource<List<ConversationDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val conversations = communicationApiService.getConversations(userId)
            emit(Resource.Success(conversations))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Une erreur s'est produite / An error occurred"))
        }
    }

    override fun getMessages(conversationId: Long): Flow<Resource<List<MessageDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val messages = communicationApiService.getMessages(conversationId)
            emit(Resource.Success(messages))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Une erreur s'est produite / An error occurred"))
        }
    }

    override fun sendMessage(message: MessageDTO): Flow<Resource<MessageDTO>> = flow {
        emit(Resource.Loading())
        try {
            val sentMessage = communicationApiService.sendMessage(message)
            emit(Resource.Success(sentMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erreur d'envoi / Sending error"))
        }
    }
}
