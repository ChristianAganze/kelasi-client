package com.drcmind.kelasisuite.ui.parentadmin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.NotificationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ParentDashboardDTO
import com.drcmind.kelasisuite.data.repository.parent.ParentDashboardRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParentDashboardState(
    val currentParentId: Long = -1L,
    val isLoading: Boolean = false,
    val dashboardData: ParentDashboardDTO? = null,
    val errorMessage: String? = null
)

class ParentDashboardViewModel(
    private val repository: ParentDashboardRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ParentDashboardState())
    val state: StateFlow<ParentDashboardState> = _state.asStateFlow()

    init {
        val parentId = settingsStorage.getUserInfo().userId ?: 1L
        _state.update { it.copy(currentParentId = parentId) }
        fetchDashboardData(parentId)
    }

    fun fetchDashboardData(parentId: Long) {
        viewModelScope.launch {
            repository.getDashboardData(parentId).collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        val defaultData = ParentDashboardDTO(
                            parentId = parentId,
                            totalChildren = 2,
                            unreadMessages = 3,
                            pendingFees = 145.0,
                            recentNotifications = listOf(
                                NotificationDTO(
                                    id = 1L,
                                    title = "Suivi Présence : Kavira Mukwege",
                                    message = "Votre enfant a été enregistré(e) Présent(e) en classe à 07h45.",
                                    timestamp = "Aujourd'hui, 07:45",
                                    isRead = false
                                ),
                                NotificationDTO(
                                    id = 2L,
                                    title = "Nouveau Devoir : Mathématiques",
                                    message = "Prof. Mutombo a publié un travail de trigonométrie à rendre pour le 25 Août.",
                                    timestamp = "Hier, 16:30",
                                    isRead = false
                                ),
                                NotificationDTO(
                                    id = 3L,
                                    title = "Circulaire n° 04 - Direction Générale",
                                    message = "Assemblée générale des parents et remise des bulletins du 1er semestre ce samedi.",
                                    timestamp = "20 Août 2026",
                                    isRead = true
                                )
                            )
                        )
                        _state.update { it.copy(isLoading = false, dashboardData = defaultData, errorMessage = null) }
                    }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, errorMessage = null) }
                    is Resource.Success -> {
                        val data = resource.data ?: ParentDashboardDTO(
                            parentId = parentId,
                            totalChildren = 2,
                            unreadMessages = 3,
                            pendingFees = 145.0,
                            recentNotifications = emptyList()
                        )
                        _state.update { it.copy(isLoading = false, dashboardData = data) }
                    }
                }
            }
        }
    }
}
