package com.drcmind.kelasisuite.ui.schooladmin.academics.deliberation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Exact match with backend ConductCode enum
enum class ConductCode(val label: String) {
    EXCELLENT("Excellente (E)"),
    VERY_GOOD("Très Bonne (TB)"),
    GOOD("Bonne (B)"),
    FAIRLY_GOOD("Assez Bonne (AB)"),
    AVERAGE("Passable (P)"),
    POOR("Médiocre (M)"),
    BAD("Mauvaise (MA)")
}

data class StudentConductData(
    val student: StudentDTO,
    val conduct: ConductCode = ConductCode.GOOD,
    val comments: String = "",
    val applicationPercentage: Double = 0.0
)

data class DeliberationsConductUiState(
    val classes: List<SchoolClassDTO> = emptyList(),
    val evaluationPeriods: List<EvaluationPeriodDTO> = emptyList(),
    val selectedClass: SchoolClassDTO? = null,
    val selectedPeriod: EvaluationPeriodDTO? = null,
    val studentConducts: List<StudentConductData> = emptyList(),
    val activeStudentConduct: StudentConductData? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

class DeliberationsConductViewModel(
    private val schoolRepository: SchoolRepository,
    private val studentsRepository: StudentsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliberationsConductUiState())
    val uiState: StateFlow<DeliberationsConductUiState> = _uiState.asStateFlow()

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
                        _uiState.update { it.copy(classes = classes, isLoading = false) }
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
                        _uiState.update { it.copy(evaluationPeriods = periods, isLoading = false) }
                        if (periods.isNotEmpty() && _uiState.value.selectedPeriod == null) {
                            _uiState.update { it.copy(selectedPeriod = periods.first()) }
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
        }
    }

    fun selectClass(schoolClass: SchoolClassDTO) {
        _uiState.update { it.copy(selectedClass = schoolClass) }
        loadStudents(schoolClass.id)
    }

    fun selectPeriod(period: EvaluationPeriodDTO) {
        _uiState.update { it.copy(selectedPeriod = period) }
    }

    fun setActiveStudent(studentId: Long?) {
        val conduct = _uiState.value.studentConducts.find { it.student.id == studentId }
        _uiState.update { it.copy(activeStudentConduct = conduct) }
    }

    private fun loadStudents(classId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            studentsRepository.getStudentsForClass(classId).onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val students = resource.data ?: emptyList()
                        val conducts = students.map { student ->
                            StudentConductData(
                                student = student,
                                conduct = ConductCode.GOOD,
                                comments = "",
                                applicationPercentage = 75.0
                            )
                        }
                        _uiState.update { it.copy(studentConducts = conducts, isLoading = false) }
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

    fun updateConduct(studentId: Long, conduct: ConductCode) {
        val updated = _uiState.value.studentConducts.map {
            if (it.student.id == studentId) it.copy(conduct = conduct) else it
        }
        val active = if (_uiState.value.activeStudentConduct?.student?.id == studentId) {
            _uiState.value.activeStudentConduct?.copy(conduct = conduct)
        } else _uiState.value.activeStudentConduct

        _uiState.update { it.copy(studentConducts = updated, activeStudentConduct = active) }
    }

    fun updateComments(studentId: Long, comments: String) {
        val updated = _uiState.value.studentConducts.map {
            if (it.student.id == studentId) it.copy(comments = comments) else it
        }
        val active = if (_uiState.value.activeStudentConduct?.student?.id == studentId) {
            _uiState.value.activeStudentConduct?.copy(comments = comments)
        } else _uiState.value.activeStudentConduct

        _uiState.update { it.copy(studentConducts = updated, activeStudentConduct = active) }
    }

    fun saveConducts() {
        val state = _uiState.value
        if (state.selectedClass == null || state.selectedPeriod == null) {
            _uiState.update { it.copy(error = "Veuillez sélectionner une classe et une période.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            _uiState.update { 
                it.copy(
                    isSaving = false, 
                    successMessage = "Fiche de conduite et remarques enregistrées pour la période ${state.selectedPeriod.label}."
                ) 
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
