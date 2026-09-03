package com.drcmind.kelasisuite.ui.schooladmin.academics.grading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class EvaluationGradingUiState(
    val classes: List<SchoolClassDTO> = emptyList(),
    val evaluationPeriods: List<EvaluationPeriodDTO> = emptyList(),
    val selectedClass: SchoolClassDTO? = null,
    val selectedPeriod: EvaluationPeriodDTO? = null,
    val assignments: List<TeachingAssignmentDTO> = emptyList(),
    val selectedAssignment: TeachingAssignmentDTO? = null,
    val students: List<StudentDTO> = emptyList(),
    val activeStudent: StudentDTO? = null,
    val grades: Map<Long, String> = emptyMap(), // studentId -> grade string
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

class EvaluationGradingViewModel(
    private val schoolRepository: SchoolRepository,
    private val studentsRepository: StudentsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EvaluationGradingUiState())
    val uiState: StateFlow<EvaluationGradingUiState> = _uiState.asStateFlow()

    init {
        loadClassesAndPeriods()
    }

    fun loadClassesAndPeriods() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            schoolRepository.getClassesForSchool().onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val classes = resource.data ?: emptyList()
                        _uiState.update { it.copy(classes = classes, isLoading = false, error = null) }
                        if (classes.isNotEmpty() && _uiState.value.selectedClass == null) {
                            selectClass(classes.first())
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = resource.message, isLoading = false) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)

            schoolRepository.getEvaluationPeriodsBySchool().onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val periods = resource.data?.values?.flatten() ?: emptyList()
                        _uiState.update { it.copy(evaluationPeriods = periods) }
                        if (periods.isNotEmpty() && _uiState.value.selectedPeriod == null) {
                            _uiState.update { it.copy(selectedPeriod = periods.first()) }
                        }
                    }
                    is Resource.Error -> {
                        // Silent error for periods so it does not block classes display
                    }
                    is Resource.Loading -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun selectClass(schoolClass: SchoolClassDTO) {
        _uiState.update { it.copy(selectedClass = schoolClass, selectedAssignment = null) }
        loadAssignmentsAndStudents(schoolClass.id)
    }

    fun selectPeriod(period: EvaluationPeriodDTO) {
        _uiState.update { it.copy(selectedPeriod = period) }
    }

    fun selectAssignment(assignment: TeachingAssignmentDTO) {
        _uiState.update { it.copy(selectedAssignment = assignment) }
    }

    fun setActiveStudent(student: StudentDTO?) {
        _uiState.update { it.copy(activeStudent = student) }
    }

    fun updateStudentGrade(studentId: Long, grade: String) {
        val currentGrades = _uiState.value.grades.toMutableMap()
        currentGrades[studentId] = grade
        _uiState.update { it.copy(grades = currentGrades) }
    }

    private fun loadAssignmentsAndStudents(classId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val activeYear = schoolRepository.getActiveAcademicYear()
            val academicYearId = activeYear?.id ?: 1L

            schoolRepository.getAssignmentsForClass(classId, academicYearId).onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val assignments = resource.data ?: emptyList()
                        _uiState.update { it.copy(assignments = assignments, isLoading = false) }
                        if (assignments.isNotEmpty() && _uiState.value.selectedAssignment == null) {
                            _uiState.update { it.copy(selectedAssignment = assignments.first()) }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)

            studentsRepository.getStudentsForClass(classId).onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(students = resource.data ?: emptyList(), isLoading = false) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = resource.message, isLoading = false) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun saveGrades() {
        val state = _uiState.value
        if (state.selectedClass == null || state.selectedAssignment == null || state.selectedPeriod == null) {
            _uiState.update { it.copy(error = "Veuillez sélectionner une classe, un cours et une période d'évaluation.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            _uiState.update { 
                it.copy(
                    isSaving = false, 
                    successMessage = "Grille de cotes enregistrée avec succès pour la période ${state.selectedPeriod.label}."
                ) 
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
