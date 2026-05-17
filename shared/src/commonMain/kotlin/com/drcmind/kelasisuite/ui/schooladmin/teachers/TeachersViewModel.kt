package com.drcmind.kelasisuite.ui.schooladmin.teachers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.domain.dto.Address
import com.drcmind.kelasisuite.domain.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class TeachersViewModel(
    private val teachersRepository: TeachersRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {
    private val _state = MutableStateFlow(TeachersUiState())
    val state: StateFlow<TeachersUiState> = _state.asStateFlow()

    private var allTeachers: List<TeacherItem> = emptyList()

    init {
        loadTeachers()
    }

    fun loadTeachers() {
        val userInfo = settingsStorage.getUserInfo()
        val schoolId = userInfo.schoolId

        if (schoolId == null) {
            _state.value = _state.value.copy(isLoading = false)
            return
        }
        teachersRepository.getTeachers(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }

                is Resource.Success -> {
                    allTeachers = resource.data?.map { it.toTeachersItem() } ?: emptyList()
                    filterTeachers()
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        filterTeachers()
    }

    private fun filterTeachers() {
        val query = _state.value.searchQuery.lowercase()
        val filtered = allTeachers.filter { teacher ->
            teacher.fullName.lowercase().contains(query) ||
                    (teacher.payrollId?.lowercase()?.contains(query) ?: false) ||
                    teacher.qualifications.lowercase().contains(query)
        }
        _state.update {
            it.copy(
                isLoading = false,
                teachers = filtered,
                totalTeachers = allTeachers.size
            )
        }
    }

    private fun TeacherProfileDTO.toTeachersItem(): TeacherItem {
        return TeacherItem(
            id = id.toString(),
            userId = userId.toString(),
            fullName = fullName,
            address = address,
            payrollId = payrollId,
            qualifications = qualifications
        )
    }

}


data class TeacherItem(
    val id: String,
    val userId: String,
    val fullName: String,
    val address: Address,
    val payrollId: String?,
    val qualifications: String,
)


data class TeachersUiState(
    val teachers: List<TeacherItem> = emptyList(),
    val totalTeachers: Int = 0,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)