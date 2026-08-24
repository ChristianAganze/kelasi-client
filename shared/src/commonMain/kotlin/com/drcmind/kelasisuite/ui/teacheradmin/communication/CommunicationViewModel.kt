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
import kotlin.time.Clock

enum class ConversationFilter(val label: String) {
    ALL("Tous"),
    PARENTS("Parents"),
    ADMIN("Direction"),
    TEACHERS("Collègues")
}

data class CommunicationState(
    val currentUserId: Long = 1L,
    val isLoading: Boolean = false,
    val conversations: List<ConversationDTO> = emptyList(),
    val selectedConversation: ConversationDTO? = null,
    val messages: List<MessageDTO> = emptyList(),
    val conversationError: String? = null,
    val messageError: String? = null,
    val sendError: String? = null,
    val searchQuery: String = "",
    val activeFilter: ConversationFilter = ConversationFilter.ALL
) {
    val filteredConversations: List<ConversationDTO>
        get() = conversations.filter { conv ->
            val receiverId = conv.participants.firstOrNull { it != currentUserId }
            val name = receiverId?.let { conv.participantNames[it] } ?: ""
            val role = receiverId?.let { conv.participantRoles[it] } ?: ""
            val lastMsg = conv.lastMessage?.content ?: ""

            val matchesFilter = when (activeFilter) {
                ConversationFilter.ALL -> true
                ConversationFilter.PARENTS -> conv.category.equals("PARENTS", ignoreCase = true)
                ConversationFilter.ADMIN -> conv.category.equals("ADMIN", ignoreCase = true)
                ConversationFilter.TEACHERS -> conv.category.equals("TEACHERS", ignoreCase = true)
            }

            val matchesQuery = searchQuery.isBlank() ||
                    name.contains(searchQuery, ignoreCase = true) ||
                    role.contains(searchQuery, ignoreCase = true) ||
                    lastMsg.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesQuery
        }
}

class CommunicationViewModel(
    private val communicationRepository: CommunicationRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(CommunicationState())
    val state: StateFlow<CommunicationState> = _state.asStateFlow()

    init {
        val userId = settingsStorage.getUserInfo().userId ?: 1L
        _state.update { it.copy(currentUserId = userId) }
        fetchConversations(userId)
    }

    fun setSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun setFilter(filter: ConversationFilter) {
        _state.update { it.copy(activeFilter = filter) }
    }

    fun retryConversations() {
        _state.update { it.copy(conversationError = null) }
        val userId = _state.value.currentUserId
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
                    is Resource.Error -> {
                        // If network fails, provide default sample conversations for rich offline demo
                        val sampleData = getSampleConversations(userId)
                        _state.update { it.copy(isLoading = false, conversations = sampleData, conversationError = null) }
                    }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, conversationError = null) }
                    is Resource.Success -> {
                        val list = if (resource.data.isNullOrEmpty()) getSampleConversations(userId) else resource.data
                        _state.update { it.copy(isLoading = false, conversations = list) }
                    }
                }
            }
        }
    }

    fun selectConversation(conversation: ConversationDTO) {
        // Mark as read locally
        val updatedConversations = _state.value.conversations.map {
            if (it.id == conversation.id) it.copy(unreadCount = 0) else it
        }
        _state.update { it.copy(selectedConversation = conversation, conversations = updatedConversations) }
        fetchMessages(conversation.id)
    }

    fun clearSelectedConversation() {
        _state.update { it.copy(selectedConversation = null, messages = emptyList()) }
    }

    private fun fetchMessages(conversationId: Long) {
        viewModelScope.launch {
            communicationRepository.getMessages(conversationId).collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        val sampleMessages = getSampleMessagesForConversation(conversationId, _state.value.currentUserId)
                        _state.update { it.copy(messages = sampleMessages, messageError = null) }
                    }
                    is Resource.Loading -> _state.update { it.copy(messageError = null) }
                    is Resource.Success -> {
                        val msgs = if (resource.data.isNullOrEmpty()) {
                            getSampleMessagesForConversation(conversationId, _state.value.currentUserId)
                        } else {
                            resource.data
                        }
                        _state.update { it.copy(messages = msgs) }
                    }
                }
            }
        }
    }

    fun sendMessage(content: String, attachmentName: String? = null, attachmentType: String? = null) {
        val selectedConv = _state.value.selectedConversation ?: return
        val currentUserId = _state.value.currentUserId
        val receiverId = selectedConv.participants.firstOrNull { it != currentUserId } ?: 2L

        val newMessage = MessageDTO(
            id = (1000..9999).random().toLong(),
            senderId = currentUserId,
            receiverId = receiverId,
            content = content,
            timestamp = Clock.System.now().toString(),
            isRead = false,
            attachmentUrl = if (attachmentName != null) "mock://documents/$attachmentName" else null,
            attachmentName = attachmentName,
            attachmentType = attachmentType
        )

        // Optimistic UI update
        _state.update { state ->
            val updatedMessages = state.messages + newMessage
            val updatedConversations = state.conversations.map { conv ->
                if (conv.id == selectedConv.id) {
                    conv.copy(lastMessage = newMessage)
                } else {
                    conv
                }
            }
            state.copy(
                messages = updatedMessages,
                conversations = updatedConversations,
                selectedConversation = state.selectedConversation?.copy(lastMessage = newMessage)
            )
        }

        viewModelScope.launch {
            communicationRepository.sendMessage(newMessage).collect { resource ->
                if (resource is Resource.Error) {
                    // Do not block optimistic flow, but record transient error if needed
                }
            }
        }
    }

    private fun getSampleConversations(currentUserId: Long): List<ConversationDTO> {
        val now = Clock.System.now().toString()
        return listOf(
            ConversationDTO(
                id = 101L,
                participants = listOf(currentUserId, 201L),
                participantNames = mapOf(currentUserId to "Moi (Enseignant)", 201L to "M. Jean Kabila"),
                participantRoles = mapOf(201L to "Parent de Marc Kabila (6ème A)"),
                participantOnlineStatus = mapOf(201L to true),
                category = "PARENTS",
                unreadCount = 2,
                lastMessage = MessageDTO(
                    id = 1L,
                    senderId = 201L,
                    receiverId = currentUserId,
                    content = "Bonjour Monsieur, Marc a-t-il bien récupéré son devoir de Mathématiques ?",
                    timestamp = now,
                    isRead = false
                )
            ),
            ConversationDTO(
                id = 102L,
                participants = listOf(currentUserId, 301L),
                participantNames = mapOf(currentUserId to "Moi (Enseignant)", 301L to "Direction des Études"),
                participantRoles = mapOf(301L to "Préfet des Études - Administration"),
                participantOnlineStatus = mapOf(301L to true),
                category = "ADMIN",
                unreadCount = 0,
                lastMessage = MessageDTO(
                    id = 2L,
                    senderId = currentUserId,
                    receiverId = 301L,
                    content = "Bien reçu, les fiches de préparation hebdomadaire sont prêtes.",
                    timestamp = now,
                    isRead = true
                )
            ),
            ConversationDTO(
                id = 103L,
                participants = listOf(currentUserId, 202L),
                participantNames = mapOf(currentUserId to "Moi (Enseignant)", 202L to "Mme. Sarah Mbemba"),
                participantRoles = mapOf(202L to "Parent de Chloé Mbemba (5ème B)"),
                participantOnlineStatus = mapOf(202L to false),
                category = "PARENTS",
                unreadCount = 0,
                lastMessage = MessageDTO(
                    id = 3L,
                    senderId = 202L,
                    receiverId = currentUserId,
                    content = "Merci pour le bulletin et les encouragements.",
                    timestamp = now,
                    isRead = true
                )
            ),
            ConversationDTO(
                id = 104L,
                participants = listOf(currentUserId, 401L),
                participantNames = mapOf(currentUserId to "Moi (Enseignant)", 401L to "Prof. Alain Mukendi"),
                participantRoles = mapOf(401L to "Enseignant de Physique-Chimie"),
                participantOnlineStatus = mapOf(401L to true),
                category = "TEACHERS",
                unreadCount = 1,
                lastMessage = MessageDTO(
                    id = 4L,
                    senderId = 401L,
                    receiverId = currentUserId,
                    content = "Est-ce qu'on peut échanger la salle de TP jeudi à 10h ?",
                    timestamp = now,
                    isRead = false
                )
            ),
            ConversationDTO(
                id = 105L,
                participants = listOf(currentUserId, 203L),
                participantNames = mapOf(currentUserId to "Moi (Enseignant)", 203L to "M. Patrick Lumumba"),
                participantRoles = mapOf(203L to "Parent de David Lumumba (6ème A)"),
                participantOnlineStatus = mapOf(203L to false),
                category = "PARENTS",
                unreadCount = 0,
                lastMessage = MessageDTO(
                    id = 5L,
                    senderId = currentUserId,
                    receiverId = 203L,
                    content = "La fiche de conduite du trimestre a été transmise.",
                    timestamp = now,
                    isRead = true
                )
            )
        )
    }

    private fun getSampleMessagesForConversation(conversationId: Long, currentUserId: Long): List<MessageDTO> {
        val now = Clock.System.now().toString()
        return when (conversationId) {
            101L -> listOf(
                MessageDTO(
                    id = 11L,
                    senderId = 201L,
                    receiverId = currentUserId,
                    content = "Bonjour Monsieur le Professeur, j'espère que vous allez bien.",
                    timestamp = now,
                    isRead = true
                ),
                MessageDTO(
                    id = 12L,
                    senderId = currentUserId,
                    receiverId = 201L,
                    content = "Bonjour M. Kabila. Tout va bien, merci. Comment puis-je vous aider ?",
                    timestamp = now,
                    isRead = true
                ),
                MessageDTO(
                    id = 13L,
                    senderId = 201L,
                    receiverId = currentUserId,
                    content = "Marc a-t-il bien récupéré son devoir de Mathématiques et le corrigé ?",
                    timestamp = now,
                    isRead = true
                ),
                MessageDTO(
                    id = 14L,
                    senderId = currentUserId,
                    receiverId = 201L,
                    content = "Oui, il a obtenu une excellente note (17/20). Voici le bulletin de synthèse ci-joint.",
                    timestamp = now,
                    isRead = true,
                    attachmentName = "Bulletin_Synthese_Marc_Kabila_T1.pdf",
                    attachmentType = "bulletin"
                ),
                MessageDTO(
                    id = 15L,
                    senderId = 201L,
                    receiverId = currentUserId,
                    content = "C'est une excellente nouvelle ! Merci infiniment pour votre suivi et votre dévouement.",
                    timestamp = now,
                    isRead = false
                )
            )
            102L -> listOf(
                MessageDTO(
                    id = 21L,
                    senderId = 301L,
                    receiverId = currentUserId,
                    content = "Chers enseignants, rappel pour la remise des fiches de préparation et journaux de classe avant vendredi 16h.",
                    timestamp = now,
                    isRead = true,
                    attachmentName = "Circulaire_Pedagogique_Mars.pdf",
                    attachmentType = "document"
                ),
                MessageDTO(
                    id = 22L,
                    senderId = currentUserId,
                    receiverId = 301L,
                    content = "Bien reçu, les fiches de préparation hebdomadaire sont prêtes.",
                    timestamp = now,
                    isRead = true
                )
            )
            else -> listOf(
                MessageDTO(
                    id = 31L,
                    senderId = currentUserId,
                    receiverId = 202L,
                    content = "Bonjour, n'hésitez pas si vous avez des questions concernant le programme.",
                    timestamp = now,
                    isRead = true
                )
            )
        }
    }
}

