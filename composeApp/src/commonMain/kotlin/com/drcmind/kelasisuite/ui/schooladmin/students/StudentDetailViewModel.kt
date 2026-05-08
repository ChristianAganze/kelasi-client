package com.drcmind.kelasisuite.ui.schooladmin.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.domain.dto.StudentDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StudentDetailViewModel(
    private val studentsRepository: StudentsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudentDetailState())
    val state: StateFlow<StudentDetailState> = _state.asStateFlow()

    fun loadStudent(studentId: Long) {
        studentsRepository.getStudent(studentId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, error = null)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, student = resource.data)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = resource.message)
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }
}

data class StudentDetailState(
    val student: StudentDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
