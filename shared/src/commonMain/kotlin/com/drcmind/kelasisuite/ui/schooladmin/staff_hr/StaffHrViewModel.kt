package com.drcmind.kelasisuite.ui.schooladmin.staff_hr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.data.repository.users.UsersRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class StaffHrUiState(
    val users: List<UserDTO> = emptyList(),
    val filteredUsers: List<UserDTO> = emptyList(),
    val selectedUser: UserDTO? = null,
    val selectedRoleFilter: String = "ALL",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class StaffHrViewModel(
    private val usersRepository: UsersRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(StaffHrUiState())
    val uiState: StateFlow<StaffHrUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        val schoolId = settingsStorage.getUserInfo().schoolId
        if (schoolId == null) {
            _uiState.update { it.copy(isLoading = false, error = "Aucun établissement associé trouvé.") }
            return
        }

        usersRepository.getUserBySchoolId(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                is Resource.Success -> {
                    val list = resource.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            users = list,
                            isLoading = false,
                            error = null
                        )
                    }
                    applyFilters()
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = resource.message
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onRoleFilterChange(role: String) {
        _uiState.update { it.copy(selectedRoleFilter = role) }
        applyFilters()
    }

    fun selectUser(user: UserDTO?) {
        _uiState.update { it.copy(selectedUser = user) }
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val roleFilter = _uiState.value.selectedRoleFilter

        val filtered = _uiState.value.users.filter { user ->
            val matchesQuery = query.isEmpty() ||
                    user.firstName.lowercase().contains(query) ||
                    user.lastName.lowercase().contains(query) ||
                    user.username.lowercase().contains(query) ||
                    (user.email?.lowercase()?.contains(query) ?: false) ||
                    (user.phone?.contains(query) ?: false)

            val matchesRole = roleFilter == "ALL" || user.roles.any { it.equals(roleFilter, ignoreCase = true) }

            matchesQuery && matchesRole
        }

        _uiState.update { it.copy(filteredUsers = filtered) }
    }
}
