package com.drcmind.kelasisuite.ui.teacheradmin.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.remote.dto.ReportCardDTO
import com.drcmind.kelasisuite.data.repository.teacher.ReportsRepository
import com.drcmind.kelasisuite.domain.util.PdfExporter
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportsState(
    val classId: Long = -1L,
    val termId: Long = -1L,
    val isLoading: Boolean = false,
    val reportCards: List<ReportCardDTO> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ReportsViewModel(
    private val reportsRepository: ReportsRepository,
    private val pdfExporter: PdfExporter
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsState())
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    fun loadReportCards(classId: Long, termId: Long) {
        _state.update { it.copy(classId = classId, termId = termId) }
        viewModelScope.launch {
            reportsRepository.getReportCards(classId, termId).collect { resource ->
                when (resource) {
                    is Resource.Error -> _state.update { it.copy(isLoading = false, errorMessage = resource.message) }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, reportCards = resource.data ?: emptyList()) }
                }
            }
        }
    }

    fun saveReportCard(reportCard: ReportCardDTO, remarks: String) {
        val updatedReportCard = reportCard.copy(teacherRemarks = remarks)
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
