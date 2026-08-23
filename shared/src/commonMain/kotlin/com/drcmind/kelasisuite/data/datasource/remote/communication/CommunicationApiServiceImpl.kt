package com.drcmind.kelasisuite.data.datasource.remote.communication

import com.drcmind.kelasisuite.data.datasource.remote.dto.ConversationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.MessageDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class CommunicationApiServiceImpl(
    private val httpClient: HttpClient
) : CommunicationApiService {
    override suspend fun getConversations(userId: Long): List<ConversationDTO> {
        return httpClient.get("communication/conversations/user/$userId").body()
    }

    override suspend fun getMessages(conversationId: Long): List<MessageDTO> {
        return httpClient.get("communication/conversations/$conversationId/messages").body()
    }

    override suspend fun sendMessage(message: MessageDTO): MessageDTO {
        return httpClient.post("communication/messages") {
            setBody(message)
        }.body()
    }
}
