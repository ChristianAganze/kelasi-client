package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.inspections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.pedagogy.SchoolInspectionRepository
import com.drcmind.kelasisuite.domain.model.pedagogy.ClassInspectionReport
import com.drcmind.kelasisuite.domain.model.pedagogy.InspectionRating
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class InspectionFormState(
    val teacherName: String = "",
    val classroomName: String = "",
    val subjectName: String = "",
    val lessonTopic: String = "",
    val inspectorName: String = "M. Mukendi Jean-Pierre",
    val inspectorRole: String = "Préfet des Études",
    val globalScore: String = "75",
    val strengths: String = "",
    val areasForImprovement: String = "",
    val recommendations: String = ""
)

data class InspectionsUiState(
    val reports: List<ClassInspectionReport> = emptyList(),
    val selectedReport: ClassInspectionReport? = null,
    val isCreateDialogOpen: Boolean = false,
    val formState: InspectionFormState = InspectionFormState(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class InspectionsViewModel(
    private val repository: SchoolInspectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InspectionsUiState())
    val uiState: StateFlow<InspectionsUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    fun selectReport(report: ClassInspectionReport) {
        _uiState.update { it.copy(selectedReport = report) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = true, formState = InspectionFormState()) }
    }

    fun closeCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = false) }
    }

    fun updateForm(transform: InspectionFormState.() -> InspectionFormState) {
        _uiState.update { it.copy(formState = it.formState.transform()) }
    }

    fun submitReport() {
        val form = _uiState.value.formState
        if (form.teacherName.isBlank() || form.classroomName.isBlank() || form.subjectName.isBlank() || form.lessonTopic.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Veuillez remplir toutes les informations obligatoires (Enseignant, Classe, Matière, Thème).") }
            return
        }

        val score = form.globalScore.toIntOrNull() ?: 70
        val rating = when {
            score >= 85 -> InspectionRating.EXCELLENT
            score >= 75 -> InspectionRating.TRES_BIEN
            score >= 60 -> InspectionRating.BIEN
            score >= 45 -> InspectionRating.A_AMELIORER
            else -> InspectionRating.INSUFFISANT
        }

        val report = ClassInspectionReport(
            teacherId = 0L,
            teacherName = form.teacherName,
            classroomName = form.classroomName,
            subjectName = form.subjectName,
            lessonTopic = form.lessonTopic,
            inspectionDate = "",
            inspectorName = form.inspectorName,
            inspectorRole = form.inspectorRole,
            globalScore = score,
            rating = rating,
            strengths = form.strengths.ifBlank { "Bonne tenue générale du cours et respect du programme." },
            areasForImprovement = form.areasForImprovement,
            recommendations = form.recommendations.ifBlank { "Poursuivre avec régularité." }
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            when (val res = repository.saveInspectionReport(report)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            isCreateDialogOpen = false,
                            successMessage = "Rapport d'inspection pédagogique enregistré avec succès."
                        )
                    }
                    loadReports()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = res.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    private fun loadReports() {
        viewModelScope.launch {
            repository.getInspectionReports(1L).collect { res ->
                if (res is Resource.Success) {
                    val list = res.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            reports = list,
                            selectedReport = it.selectedReport ?: list.firstOrNull()
                        )
                    }
                }
            }
        }
    }
}
