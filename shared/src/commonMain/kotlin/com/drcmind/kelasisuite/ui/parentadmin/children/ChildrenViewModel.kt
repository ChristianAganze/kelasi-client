package com.drcmind.kelasisuite.ui.parentadmin.children

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.AttendanceDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ChildDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeDTO
import com.drcmind.kelasisuite.data.repository.parent.ParentChildrenRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChildrenState(
    val currentParentId: Long = -1L,
    val isLoadingChildren: Boolean = false,
    val children: List<ChildDTO> = emptyList(),
    val childrenError: String? = null,

    val selectedChildId: Long? = null,
    val isLoadingDetails: Boolean = false,
    val attendance: List<AttendanceDTO> = emptyList(),
    val grades: List<GradeDTO> = emptyList(),
    val detailsError: String? = null
)

class ChildrenViewModel(
    private val repository: ParentChildrenRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ChildrenState())
    val state: StateFlow<ChildrenState> = _state.asStateFlow()

    init {
        val parentId = settingsStorage.getUserInfo().userId
        if (parentId != null) {
            _state.update { it.copy(currentParentId = parentId) }
            fetchChildren(parentId)
        } else {
            _state.update { it.copy(childrenError = "Utilisateur non connecté") }
        }
    }

    fun fetchChildren(parentId: Long) {
        viewModelScope.launch {
            repository.getChildren(parentId).collect { resource ->
                when (resource) {
                    is Resource.Error -> _state.update { it.copy(isLoadingChildren = false, childrenError = resource.message) }
                    is Resource.Loading -> _state.update { it.copy(isLoadingChildren = true, childrenError = null) }
                    is Resource.Success -> _state.update { it.copy(isLoadingChildren = false, children = resource.data ?: emptyList()) }
                }
            }
        }
    }

    fun selectChild(childId: Long) {
        _state.update { it.copy(selectedChildId = childId) }
        fetchChildDetails(childId)
    }

    private fun fetchChildDetails(childId: Long) {
        viewModelScope.launch {
            // Fetch attendance
            launch {
                repository.getChildAttendance(childId).collect { resource ->
                    when (resource) {
                        is Resource.Error -> _state.update { it.copy(isLoadingDetails = false, detailsError = resource.message) }
                        is Resource.Loading -> _state.update { it.copy(isLoadingDetails = true, detailsError = null) }
                        is Resource.Success -> _state.update { it.copy(isLoadingDetails = false, attendance = resource.data ?: emptyList()) }
                    }
                }
            }
            // Fetch grades
            launch {
                repository.getChildGrades(childId).collect { resource ->
                    when (resource) {
                        is Resource.Error -> _state.update { it.copy(isLoadingDetails = false, detailsError = resource.message) }
                        is Resource.Loading -> _state.update { it.copy(isLoadingDetails = true, detailsError = null) }
                        is Resource.Success -> _state.update { it.copy(isLoadingDetails = false, grades = resource.data ?: emptyList()) }
                    }
                }
            }
        }
    }
}
