package com.drcmind.kelasisuite.ui.parentadmin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
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
        val parentId = settingsStorage.getUserInfo().userId
        if (parentId != null) {
            _state.update { it.copy(currentParentId = parentId) }
            fetchDashboardData(parentId)
        } else {
            _state.update { it.copy(errorMessage = "Utilisateur non connecté") }
        }
    }

    fun fetchDashboardData(parentId: Long) {
        viewModelScope.launch {
            repository.getDashboardData(parentId).collect { resource ->
                when (resource) {
                    is Resource.Error -> _state.update { it.copy(isLoading = false, errorMessage = resource.message) }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, errorMessage = null) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, dashboardData = resource.data) }
                }
            }
        }
    }
}
