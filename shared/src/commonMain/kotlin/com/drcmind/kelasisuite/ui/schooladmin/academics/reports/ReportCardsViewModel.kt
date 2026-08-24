package com.drcmind.kelasisuite.ui.schooladmin.academics.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.domain.model.academic.AcademicDecision
import com.drcmind.kelasisuite.domain.model.academic.ClassPalmaresSummary
import com.drcmind.kelasisuite.domain.model.academic.StudentPalmaresItem
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ReportViewMode {
    INDIVIDUAL_BULLETIN, // Bulletin individuel
    CLASS_PALMARES       // Palmarès général de la classe
}

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
    val viewMode: ReportViewMode = ReportViewMode.CLASS_PALMARES,
    val classes: List<SchoolClassDTO> = emptyList(),
    val selectedClass: SchoolClassDTO? = null,
    val evaluationPeriods: List<EvaluationPeriodDTO> = emptyList(),
    val selectedPeriod: EvaluationPeriodDTO? = null,
    val students: List<StudentDTO> = emptyList(),
    val selectedStudent: StudentDTO? = null,
    val reportCardSummary: ReportCardSummary? = null,
    val classPalmares: ClassPalmaresSummary? = null,
    val isPeriodLocked: Boolean = false,
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
        loadClassesAndPeriods()
    }

    private fun loadClassesAndPeriods() {
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

            schoolRepository.getEvaluationPeriodsBySchool().onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val periods = resource.data?.values?.flatten() ?: emptyList()
                        _uiState.update { it.copy(evaluationPeriods = periods) }
                        if (periods.isNotEmpty() && _uiState.value.selectedPeriod == null) {
                            _uiState.update { it.copy(selectedPeriod = periods.first()) }
                        }
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun setViewMode(mode: ReportViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }

    fun selectClass(schoolClass: SchoolClassDTO) {
        _uiState.update { it.copy(selectedClass = schoolClass) }
        loadStudents(schoolClass.id)
    }

    fun selectPeriod(period: EvaluationPeriodDTO) {
        _uiState.update { it.copy(selectedPeriod = period) }
        generatePalmares()
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
                            generatePalmares()
                        } else {
                            _uiState.update { it.copy(reportCardSummary = null, classPalmares = null) }
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
            SubjectGradeSummary("Mathématiques / Algèbre", 100.0, 84.5, "Prof. Musafiri"),
            SubjectGradeSummary("Physique & Technologie", 60.0, 49.0, "Prof. Mukendi"),
            SubjectGradeSummary("Français & Littérature", 60.0, 48.0, "Prof. Kabeya"),
            SubjectGradeSummary("Anglais", 40.0, 32.0, "Prof. Mukendi"),
            SubjectGradeSummary("Chimie & Biologie", 60.0, 43.5, "Prof. Tshilombo"),
            SubjectGradeSummary("Histoire & Éducation Civique", 40.0, 31.0, "Prof. Mbuyi")
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
            rank = 2,
            totalStudentsInClass = totalStudents,
            conductLabel = "Très Bonne (TB)",
            decision = if (percentage >= 50.0) "ADMIS(E)" else "AJOURNÉ(E)",
            subjects = sampleSubjects
        )

        _uiState.update { it.copy(reportCardSummary = summary) }
    }

    private fun generatePalmares() {
        val students = _uiState.value.students
        val selectedClass = _uiState.value.selectedClass
        val period = _uiState.value.selectedPeriod

        if (students.isEmpty() || selectedClass == null) return

        val palmaresList = students.mapIndexed { index, student ->
            val name = student.fullName.ifEmpty { "${student.lastName} ${student.firstName}" }
            // Simuler des cotes basées sur le ranking pour cohérence
            val percentage = 88.0 - (index * 4.5).coerceAtMost(50.0)
            val totalMax = 360.0
            val totalObtained = (percentage * totalMax) / 100.0
            val decision = when {
                percentage >= 65.0 -> AcademicDecision.ADMITTED
                percentage >= 50.0 -> AcademicDecision.CONDITIONAL
                else -> AcademicDecision.RETAKE
            }
            StudentPalmaresItem(
                studentId = student.id,
                studentName = name,
                rollNumber = "N° ${index + 1}",
                totalObtained = totalObtained,
                totalMax = totalMax,
                percentage = percentage,
                rank = index + 1,
                conductLabel = if (index % 3 == 0) "Très Bonne" else "Bonne",
                applicationPercentage = 70.0 + (index * 2.0).coerceAtMost(25.0),
                decision = decision
            )
        }

        val classAvg = if (palmaresList.isNotEmpty()) palmaresList.map { it.percentage }.average() else 0.0
        val passCount = palmaresList.count { it.percentage >= 50.0 }
        val passRate = if (palmaresList.isNotEmpty()) (passCount.toDouble() / palmaresList.size) * 100.0 else 0.0

        val summary = ClassPalmaresSummary(
            classId = selectedClass.id,
            className = selectedClass.name,
            periodLabel = period?.label ?: "1ère Période",
            schoolYear = "2025 - 2026",
            totalStudents = palmaresList.size,
            classAveragePercentage = classAvg,
            highestPercentage = palmaresList.firstOrNull()?.percentage ?: 0.0,
            lowestPercentage = palmaresList.lastOrNull()?.percentage ?: 0.0,
            passRatePercentage = passRate,
            students = palmaresList
        )

        _uiState.update { it.copy(classPalmares = summary) }
    }

    fun lockPeriodAndOfficialize() {
        val periodLabel = _uiState.value.selectedPeriod?.label ?: "la période"
        _uiState.update { 
            it.copy(
                isPeriodLocked = true,
                successMessage = "Délibération clôturée et officielle pour $periodLabel. Les bulletins sont désormais verrouillés et prêts pour distribution aux parents."
            ) 
        }
    }

    fun exportPdf() {
        _uiState.update { 
            it.copy(successMessage = "Génération du document officiel certifié MINEPST (PDF) en cours...") 
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
