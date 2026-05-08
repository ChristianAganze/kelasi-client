package com.drcmind.kelasisuite.ui.schooladmin.academicManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ClassesViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ClassesState())
    val state: StateFlow<ClassesState> = _state.asStateFlow()

    private val schoolId: Long = 1 // Placeholder

    init {
        loadClasses()
    }

    fun loadClasses() {
        schoolRepository.getClasses(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        classes = resource.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = resource.message
                    )
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun deleteClass(classId: Long) {
        schoolRepository.deleteClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isDeleting = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(isDeleting = false)
                    loadClasses() // Refresh list
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isDeleting = false,
                        errorMessage = resource.message
                    )
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }
}

data class ClassesState(
    val classes: List<SchoolClassDTO> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null
)
