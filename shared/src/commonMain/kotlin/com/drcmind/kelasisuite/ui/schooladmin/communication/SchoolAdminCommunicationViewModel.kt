package com.drcmind.kelasisuite.ui.schooladmin.communication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.communication.SchoolAdminCommunicationRepository
import com.drcmind.kelasisuite.domain.model.communication.*
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AdminCommTab(val label: String) {
    ANNOUNCEMENTS("Canal Officiel & Circulaires"),
    DIRECT_MESSAGING("Messagerie Directe Parents")
}

data class AnnouncementFormState(
    val title: String = "",
    val summary: String = "",
    val content: String = "",
    val type: AnnouncementType = AnnouncementType.CIRCULAR,
    val priority: AnnouncementPriority = AnnouncementPriority.NORMAL,
    val audience: AnnouncementAudience = AnnouncementAudience.ALL,
    val signedByTitle: String = "La Direction Générale",
    val isPinned: Boolean = false
)

data class SchoolAdminCommUiState(
    val activeTab: AdminCommTab = AdminCommTab.ANNOUNCEMENTS,
    val announcements: List<SchoolOfficialAnnouncement> = emptyList(),
    val selectedAnnouncement: SchoolOfficialAnnouncement? = null,
    val parentConversations: List<DirectParentMessage> = emptyList(),
    val selectedConversation: DirectParentMessage? = null,
    val replyMessageText: String = "",
    val isCreateDialogOpen: Boolean = false,
    val formState: AnnouncementFormState = AnnouncementFormState(),
    val isSubmitting: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class SchoolAdminCommunicationViewModel(
    private val repository: SchoolAdminCommunicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SchoolAdminCommUiState())
    val uiState: StateFlow<SchoolAdminCommUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun setTab(tab: AdminCommTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun selectAnnouncement(announcement: SchoolOfficialAnnouncement) {
        _uiState.update { it.copy(selectedAnnouncement = announcement) }
    }

    fun selectConversation(conversation: DirectParentMessage) {
        _uiState.update { it.copy(selectedConversation = conversation, replyMessageText = "") }
    }

    fun updateReplyText(text: String) {
        _uiState.update { it.copy(replyMessageText = text) }
    }

    fun sendReply() {
        val conv = _uiState.value.selectedConversation ?: return
        val text = _uiState.value.replyMessageText.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.sendDirectMessage(conv.parentId, text)
            _uiState.update {
                it.copy(
                    replyMessageText = "",
                    successMessage = "Message transmis avec succès à ${conv.parentName}."
                )
            }
            loadData()
        }
    }

    fun togglePin(announcementId: Long) {
        viewModelScope.launch {
            repository.togglePinAnnouncement(announcementId)
            loadData()
        }
    }

    fun openCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = true, formState = AnnouncementFormState()) }
    }

    fun closeCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = false) }
    }

    fun updateForm(transform: AnnouncementFormState.() -> AnnouncementFormState) {
        _uiState.update { it.copy(formState = it.formState.transform()) }
    }

    fun submitAnnouncement() {
        val form = _uiState.value.formState
        if (form.title.isBlank() || form.content.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Veuillez remplir au moins le titre et le contenu du communiqué.") }
            return
        }

        val announcement = SchoolOfficialAnnouncement(
            title = form.title,
            summary = form.summary.ifBlank { form.title },
            content = form.content,
            type = form.type,
            priority = form.priority,
            audience = form.audience,
            publishedDate = "",
            publishedBy = "Direction de l'Établissement",
            signedByTitle = form.signedByTitle,
            isPinned = form.isPinned
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            when (val res = repository.publishAnnouncement(announcement)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            isCreateDialogOpen = false,
                            successMessage = "Communiqué officiel publié et diffusé sur les portails Parents & Enseignants."
                        )
                    }
                    loadData()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = res.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getOfficialAnnouncements(1L).collect { res ->
                if (res is Resource.Success) {
                    val list = res.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            announcements = list,
                            selectedAnnouncement = it.selectedAnnouncement ?: list.firstOrNull()
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.getParentConversations(1L).collect { res ->
                if (res is Resource.Success) {
                    val list = res.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            parentConversations = list,
                            selectedConversation = it.selectedConversation ?: list.firstOrNull()
                        )
                    }
                }
            }
        }
    }
}
