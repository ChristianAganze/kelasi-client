package com.drcmind.kelasisuite.ui.teacheradmin.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ReportCardDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.teacher.ReportsRepository
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.domain.util.PdfExporter
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportsState(
    val availableClasses: List<TeachingAssignmentDTO> = emptyList(),
    val selectedClass: TeachingAssignmentDTO? = null,
    val evaluationPeriods: List<EvaluationPeriodDTO> = emptyList(),
    val selectedPeriod: EvaluationPeriodDTO? = null,
    val isLoading: Boolean = false,
    val reportCards: List<ReportCardDTO> = emptyList(),
    val loadError: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ReportsViewModel(
    private val settingsStorage: SettingsStorage,
    private val teachersRepository: TeachersRepository,
    private val assignmentRepository: AssignmentRepository,
    private val schoolRepository: SchoolRepository,
    private val reportsRepository: ReportsRepository,
    private val pdfExporter: PdfExporter
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsState())
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    init {
        fetchTeacherClasses()
        fetchEvaluationPeriods()
    }

    fun retry() {
        _state.update { it.copy(loadError = null) }
        fetchTeacherClasses()
        fetchEvaluationPeriods()
        loadReportCardsIfReady()
    }

    private fun fetchTeacherClasses() {
        val schoolId = settingsStorage.getSchool()?.id
        val userId = settingsStorage.getUserInfo().userId
        if (schoolId == null || userId == null) {
            _state.update {
                it.copy(loadError = "Connexion incomplète : impossible de charger les bulletins.")
            }
            return
        }

        viewModelScope.launch {
            teachersRepository.getTeachers(schoolId).collect { teachersResource ->
                when (teachersResource) {
                    is Resource.Error -> _state.update { it.copy(loadError = teachersResource.message) }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val myProfile = teachersResource.data?.find { it.userId == userId }
                        if (myProfile != null) {
                            assignmentRepository.getAssignmentsForSchool().collect { assignmentsResource ->
                                when (assignmentsResource) {
                                    is Resource.Error -> _state.update { it.copy(loadError = assignmentsResource.message) }
                                    is Resource.Loading -> {}
                                    is Resource.Success -> {
                                        val myAssignments =
                                            assignmentsResource.data?.filter { it.teacherId == myProfile.id } ?: emptyList()
                                        _state.update { it.copy(availableClasses = myAssignments) }
                                    }
                                }
                            }
                        } else {
                            _state.update { it.copy(loadError = "Profil enseignant introuvable.") }
                        }
                    }
                }
            }
        }
    }

    private fun fetchEvaluationPeriods() {
        viewModelScope.launch {
            schoolRepository.getEvaluationPeriodsBySchool().collect { resource ->
                when (resource) {
                    is Resource.Error -> _state.update { it.copy(loadError = resource.message) }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val periods = resource.data?.values?.flatten() ?: emptyList()
                        _state.update { it.copy(evaluationPeriods = periods) }
                    }
                }
            }
        }
    }

    fun selectClass(assignment: TeachingAssignmentDTO) {
        _state.update { it.copy(selectedClass = assignment, reportCards = emptyList()) }
        loadReportCardsIfReady()
    }

    fun selectPeriod(period: EvaluationPeriodDTO) {
        _state.update { it.copy(selectedPeriod = period, reportCards = emptyList()) }
        loadReportCardsIfReady()
    }

    private fun loadReportCardsIfReady() {
        val classId = _state.value.selectedClass?.classId
        val periodId = _state.value.selectedPeriod?.id
        if (classId != null && periodId != null) {
            loadReportCards(classId, periodId)
        }
    }

    private fun loadReportCards(classId: Long, termId: Long) {
        viewModelScope.launch {
            reportsRepository.getReportCards(classId, termId).collect { resource ->
                when (resource) {
                    is Resource.Error -> _state.update { it.copy(isLoading = false, loadError = resource.message) }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, loadError = null, successMessage = null) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, reportCards = resource.data ?: emptyList()) }
                }
            }
        }
    }

    fun saveReportCard(reportCard: ReportCardDTO, remarks: String, conduct: String) {
        val updatedReportCard = reportCard.copy(
            teacherRemarks = remarks,
            studentConduct = conduct.ifBlank { null }
        )
        viewModelScope.launch {
            reportsRepository.saveReportCard(updatedReportCard).collect { resource ->
                when (resource) {
                    is Resource.Error -> _state.update { it.copy(isLoading = false, errorMessage = resource.message) }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                    is Resource.Success -> {
                        resource.data?.let { savedCard ->
                            _state.update { currentState ->
                                val newList = currentState.reportCards.map { if (it.id == savedCard.id) savedCard else it }
                                currentState.copy(isLoading = false, reportCards = newList, successMessage = "Bulletin sauvegardé avec succès")
                            }
                        }
                    }
                }
            }
        }
    }

    fun exportReportCard(reportCard: ReportCardDTO) {
        val title = "Bulletin de ${reportCard.studentName}"
        val content = """
            Élève: ${reportCard.studentName}
            Total: ${reportCard.totalScore} / ${reportCard.maxScore}
            Moyenne: ${reportCard.average}%
            Appréciation: ${reportCard.teacherRemarks ?: "Aucune"}
        """.trimIndent()

        val isSuccess = pdfExporter.exportToPdf(
            title = title,
            content = content,
            fileName = "Bulletin_${reportCard.studentName.replace(" ", "_")}.pdf"
        )

        if (isSuccess) {
            _state.update { it.copy(successMessage = "Bulletin exporté en PDF avec succès") }
        } else {
            _state.update { it.copy(errorMessage = "Échec de l'exportation PDF") }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
