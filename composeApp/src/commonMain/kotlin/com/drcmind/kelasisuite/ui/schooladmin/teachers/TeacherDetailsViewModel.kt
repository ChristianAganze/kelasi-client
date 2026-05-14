package com.drcmind.kelasisuite.ui.schooladmin.teachers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.domain.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TeacherDetailsViewModel(
    private val teachersRepository: TeachersRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TeacherDetailsState())
    val state: StateFlow<TeacherDetailsState> = _state.asStateFlow()

    fun loadTeacher(teacherId: Long) {
        teachersRepository.getTeacher(teacherId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, error = null)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, teacher = resource.data)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = resource.message)
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }
}

data class TeacherDetailsState(
    val teacher: TeacherProfileDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
