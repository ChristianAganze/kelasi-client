package com.drcmind.kelasisuite.ui.schooladmin.academicManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.domain.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.domain.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AddClassViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AddClassState())
    val state: StateFlow<AddClassState> = _state.asStateFlow()

    // Assuming a hardcoded schoolId for now, ideally this comes from session/settings
    private val schoolId: Long = 1

    init {
        loadSections()
    }

    fun loadClassDetails(classId: Long) {
        // In a real app, we'd fetch the class details from the repository
        // and update the state. For now, assuming we might already have it or fetch it.
        _state.value = _state.value.copy(isLoading = true)
        schoolRepository.getClasses(schoolId).onEach { resource ->
            if (resource is Resource.Success) {
                val clazz = resource.data?.find { it.id == classId }
                if (clazz != null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        name = clazz.name,
                        capacity = clazz.capacity?.toString() ?: "30",
                        selectedSection = _state.value.sections.find { it.name == clazz.sectionName }
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadSections() {
        schoolRepository.getSchoolSections(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        sections = resource.data ?: emptyList(),
                        selectedSection = resource.data?.firstOrNull()
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

    fun onNameChange(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun onSectionChange(section: SchoolSectionDTO) {
        _state.value = _state.value.copy(selectedSection = section)
    }

    fun onCapacityChange(capacity: String) {
        _state.value = _state.value.copy(capacity = capacity)
    }

    fun createClass() {
        val currentState = _state.value
        val sectionId = currentState.selectedSection?.id ?: return
        
        val request = CreateClassFromTemplateRequest(
            schoolId = schoolId,
            templateGradeLevelId = sectionId, // Adjust based on real API logic
            name = currentState.name,
            capacity = currentState.capacity.toIntOrNull() ?: 30
        )

        schoolRepository.createClass(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isSaving = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(isSaving = false, isSuccess = true)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = resource.message
                    )
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun updateClass(classId: Long) {
        val currentState = _state.value
        val sectionId = currentState.selectedSection?.id ?: return

        val request = CreateClassFromTemplateRequest(
            schoolId = schoolId,
            templateGradeLevelId = sectionId,
            name = currentState.name,
            capacity = currentState.capacity.toIntOrNull() ?: 30
        )

        schoolRepository.updateClass(classId, request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isSaving = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(isSaving = false, isSuccess = true)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = resource.message
                    )
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }
}

data class AddClassState(
    val sections: List<SchoolSectionDTO> = emptyList(),
    val selectedSection: SchoolSectionDTO? = null,
    val name: String = "",
    val capacity: String = "30",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
