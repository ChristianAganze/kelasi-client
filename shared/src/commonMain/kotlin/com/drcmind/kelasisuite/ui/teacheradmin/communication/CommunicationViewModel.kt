package com.drcmind.kelasisuite.ui.teacheradmin.communication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.ConversationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.MessageDTO
import com.drcmind.kelasisuite.data.repository.communication.CommunicationRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CommunicationState(
    val currentUserId: Long = -1L,
    val isLoading: Boolean = false,
    val conversations: List<ConversationDTO> = emptyList(),
    val selectedConversation: ConversationDTO? = null,
    val messages: List<MessageDTO> = emptyList(),
    val conversationError: String? = null,
    val messageError: String? = null,
    val sendError: String? = null
)

class CommunicationViewModel(
    private val communicationRepository: CommunicationRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(CommunicationState())
    val state: StateFlow<CommunicationState> = _state.asStateFlow()

    init {
        val userId = settingsStorage.getUserInfo().userId
        if (userId != null) {
            _state.update { it.copy(currentUserId = userId) }
            fetchConversations(userId)
        } else {
            _state.update { it.copy(conversationError = "Utilisateur non connecté") }
        }
    }

    fun retryConversations() {
        _state.update { it.copy(conversationError = null) }
        val userId = settingsStorage.getUserInfo().userId ?: return
        fetchConversations(userId)
    }

    fun retryMessages() {
        _state.update { it.copy(messageError = null) }
        val conversationId = _state.value.selectedConversation?.id ?: return
        fetchMessages(conversationId)
    }

    fun dismissSendError() {
        _state.update { it.copy(sendError = null) }
    }

    private fun fetchConversations(userId: Long) {
        viewModelScope.launch {
            communicationRepository.getConversations(userId).collect { resource ->
                when (resource) {
                    is Resource.Error -> _state.update { it.copy(isLoading = false, conversationError = resource.message) }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, conversationError = null) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, conversations = resource.data ?: emptyList()) }
                }
            }
        }
    }

    fun selectConversation(conversation: ConversationDTO) {
        _state.update { it.copy(selectedConversation = conversation) }
        fetchMessages(conversation.id)
    }

    private fun fetchMessages(conversationId: Long) {
        viewModelScope.launch {
            communicationRepository.getMessages(conversationId).collect { resource ->
                when (resource) {
                    is Resource.Error -> _state.update { it.copy(messageError = resource.message) }
                    is Resource.Loading -> _state.update { it.copy(messageError = null) }
                    is Resource.Success -> _state.update { it.copy(messages = resource.data ?: emptyList()) }
                }
            }
        }
    }

    fun sendMessage(content: String) {
        val selectedConv = _state.value.selectedConversation ?: return
        val currentUserId = _state.value.currentUserId
        val receiverId = selectedConv.participants.firstOrNull { it != currentUserId } ?: return

        val newMessage = MessageDTO(
            senderId = currentUserId,
            receiverId = receiverId,
            content = content,
            timestamp = kotlin.time.Clock.System.now().toString()
        )

        viewModelScope.launch {
            communicationRepository.sendMessage(newMessage).collect { resource ->
                when (resource) {
                    is Resource.Error -> _state.update { it.copy(sendError = resource.message) }
                    is Resource.Loading -> { /* Do nothing visually to keep it fast */ }
                    is Resource.Success -> {
                        resource.data?.let { sentMessage ->
                            _state.update { it.copy(messages = it.messages + sentMessage) }
                        }
                    }
                }
            }
        }
    }
}
