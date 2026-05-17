package com.drcmind.kelasisuite.ui.schooladmin.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.domain.dto.StudentDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class StudentsViewModel(
    private val studentsRepository: StudentsRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {
    private val _state = MutableStateFlow(StudentUiState())
    val state: StateFlow<StudentUiState> = _state.asStateFlow()

    private var allStudents: List<StudentItem> = emptyList()

    init {
        loadStudents()
    }

    fun loadStudents() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        studentsRepository.getStudents(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }

                is Resource.Success -> {
                    allStudents = resource.data?.map { it.toStudentItem() } ?: emptyList()
                    filterStudents()
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
        filterStudents()
    }

    private fun filterStudents() {
        val query = _state.value.searchQuery.lowercase()
        val filtered = allStudents.filter { student ->
            student.name.lowercase().contains(query) ||
                    student.matricule.lowercase().contains(query) ||
                    student.className.lowercase().contains(query)
        }
        _state.update {
            it.copy(
                isLoading = false,
                students = filtered,
                totalStudents = allStudents.size
            )
        }
    }

    private fun StudentDTO.toStudentItem(): StudentItem {
        return StudentItem(
            id = id.toString(),
            name = fullName,
            matricule = studentIdNumber,
            className = currentEnrollment?.className ?: "Non assigné",
            adress = address
                ?: "N/A", // Reusing GPA field as address for the table as per current UI
            status = status,
            dateOfBirth = dateOfBirth.toString()
        )
    }

    fun onAddStudent() { /* Logique d'ajout */
    }
}


data class StudentItem(
    val id: String,
    val name: String,
    val matricule: String,
    val className: String,
    val adress: String,
    val dateOfBirth: String,
    val status: StudentStatus
)

enum class StudentStatus { ACTIVE, PROBATION, INACTIVE }

data class StudentUiState(
    val students: List<StudentItem> = emptyList(),
    val totalStudents: Int = 0,
    val newStudents: Int = 0,
    val pendingActionCount: Int = 0,
    val graduateCount: Int = 0,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)