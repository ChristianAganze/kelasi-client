package com.drcmind.kelasisuite.ui.schooladmin.academics.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SubjectGradeSummary(
    val subjectName: String,
    val maxPoints: Double,
    val obtainedPoints: Double,
    val teacherName: String
)

data class ReportCardSummary(
    val student: StudentDTO,
    val className: String,
    val schoolYear: String,
    val totalObtained: Double,
    val totalMax: Double,
    val percentage: Double,
    val rank: Int,
    val totalStudentsInClass: Int,
    val conductLabel: String,
    val decision: String,
    val subjects: List<SubjectGradeSummary>
)

data class ReportCardsUiState(
    val classes: List<SchoolClassDTO> = emptyList(),
    val selectedClass: SchoolClassDTO? = null,
    val students: List<StudentDTO> = emptyList(),
    val selectedStudent: StudentDTO? = null,
    val reportCardSummary: ReportCardSummary? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class ReportCardsViewModel(
    private val schoolRepository: SchoolRepository,
    private val studentsRepository: StudentsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportCardsUiState())
    val uiState: StateFlow<ReportCardsUiState> = _uiState.asStateFlow()

    init {
        loadClasses()
    }

    private fun loadClasses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

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
                    is Resource.Loading -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun selectClass(schoolClass: SchoolClassDTO) {
        _uiState.update { it.copy(selectedClass = schoolClass) }
        loadStudents(schoolClass.id)
    }

    private fun loadStudents(classId: Long) {
        viewModelScope.launch {
            studentsRepository.getStudentsForClass(classId).onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val students = resource.data ?: emptyList()
                        _uiState.update { it.copy(students = students) }
                        if (students.isNotEmpty()) {
                            selectStudent(students.first())
                        } else {
                            _uiState.update { it.copy(reportCardSummary = null) }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = resource.message) }
                    }
                    is Resource.Loading -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun selectStudent(student: StudentDTO) {
        _uiState.update { it.copy(selectedStudent = student) }
        generateReportCardSummary(student)
    }

    private fun generateReportCardSummary(student: StudentDTO) {
        val selectedClass = _uiState.value.selectedClass
        val totalStudents = _uiState.value.students.size

        val sampleSubjects = listOf(
            SubjectGradeSummary("Mathématiques / Physique", 100.0, 82.5, "Prof. Musafiri"),
            SubjectGradeSummary("Français & Expression", 60.0, 48.0, "Prof. Kabeya"),
            SubjectGradeSummary("Anglais", 40.0, 31.0, "Prof. Mukendi"),
            SubjectGradeSummary("Chimie & Biologie", 60.0, 42.0, "Prof. Tshilombo"),
            SubjectGradeSummary("Histoire & Géographie", 40.0, 30.5, "Prof. Mbuyi")
        )

        val totalMax = sampleSubjects.sumOf { it.maxPoints }
        val totalObtained = sampleSubjects.sumOf { it.obtainedPoints }
        val percentage = (totalObtained / totalMax) * 100.0

        val summary = ReportCardSummary(
            student = student,
            className = selectedClass?.name ?: "Classe",
            schoolYear = "2025 - 2026",
            totalObtained = totalObtained,
            totalMax = totalMax,
            percentage = percentage,
            rank = 3,
            totalStudentsInClass = totalStudents,
            conductLabel = "Bonne (B)",
            decision = if (percentage >= 50.0) "ADMIS(E)" else "A_REPRENDRE",
            subjects = sampleSubjects
        )

        _uiState.update { it.copy(reportCardSummary = summary) }
    }

    fun exportPdf() {
        _uiState.update { 
            it.copy(successMessage = "Génération du bulletin officiel PDF démarrée. Téléchargement en cours...") 
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
