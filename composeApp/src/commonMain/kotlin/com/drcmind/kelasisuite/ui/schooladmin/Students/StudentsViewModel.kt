package com.drcmind.kelasisuite.ui.schooladmin.Students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentsViewModel : ViewModel() {
    private val _state = MutableStateFlow(StudentUiState())
    val state: StateFlow<StudentUiState> = _state.asStateFlow()

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            // Simulation d'un chargement API
            val mockStudents = listOf(
                StudentItem("1", "Jean-Baptiste Kim", "jb.kim@student.cd", "#KL-2023-0842", "3e Scientifique B", 0.94f, "Qu. Ndendere n°12", StudentStatus.ACTIVE),
                StudentItem("2", "Sarah Mubiala", "s.mubiala@student.cd", "#KL-2023-0129", "3e Scientifique B", 0.72f, "Qu. Ndendere n°12", StudentStatus.PROBATION),
                StudentItem("3", "Marc Bolongo", "m.bolongo@student.cd", "#KL-2022-0941", "3e Scientifique B", 0.98f, "Qu. Ndendere n°12", StudentStatus.ACTIVE),
                StudentItem("4", "Marie-Ange Lelo", "ma.lelo@student.cd", "#KL-2023-0056", "3e Scientifique B", 0.00f, "N/A", StudentStatus.INACTIVE)
            )
            _state.value = _state.value.copy(students = mockStudents)
        }
    }

    fun onAddStudent() { /* Logique d'ajout */ }
}








data class StudentItem(
    val id: String,
    val name: String,
    val email: String,
    val matricule: String,
    val className: String,
    val attendance: Float, // 0.0f to 1.0f
    val gpa: String,
    val status: StudentStatus
)

enum class StudentStatus { ACTIVE, PROBATION, INACTIVE }

data class StudentUiState(
    val students: List<StudentItem> = emptyList(),
    val totalStudents: Int = 1284,
    val newStudents: Int = 156,
    val pendingActionCount: Int = 24,
    val graduateCount: Int = 412,
    val isLoading: Boolean = false
)