package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.preparation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.remote.dto.PreparationReviewDto
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

enum class PreparationFilter {
    ALL,
    PENDING,
    VALIDATED,
    REJECTED
}

data class PreparationsUiState(
    val preparations: List<PreparationReviewDto> = emptyList(),
    val filter: PreparationFilter = PreparationFilter.ALL,
    val selectedPreparation: PreparationReviewDto? = null,
    val showRejectDialog: Boolean = false,
    val rejectComment: String = "",
    val isActionInProgress: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PreparationsViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreparationsUiState())
    val uiState: StateFlow<PreparationsUiState> = _uiState.asStateFlow()

    init {
        loadPreparations()
    }

    fun loadPreparations() {
        schoolRepository.getPreparationsForReview().onEach { resource ->
            _uiState.update { state ->
                when (resource) {
                    is Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> state.copy(
                        preparations = resource.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                    is Resource.Error -> state.copy(isLoading = false, error = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setFilter(filter: PreparationFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun openDetail(preparation: PreparationReviewDto) {
        _uiState.update { it.copy(selectedPreparation = preparation, error = null) }
    }

    fun dismissDetail() {
        _uiState.update { it.copy(selectedPreparation = null) }
    }

    fun showRejectDialog() {
        _uiState.update { it.copy(showRejectDialog = true, rejectComment = "") }
    }

    fun dismissRejectDialog() {
        _uiState.update { it.copy(showRejectDialog = false, rejectComment = "") }
    }

    fun updateRejectComment(comment: String) {
        _uiState.update { it.copy(rejectComment = comment) }
    }

    fun validatePreparation() {
        val target = _uiState.value.selectedPreparation ?: return
        reviewPreparation(target, validate = true, comment = null)
    }

    fun rejectPreparation() {
        val target = _uiState.value.selectedPreparation ?: return
        val comment = _uiState.value.rejectComment.ifBlank { null }
        reviewPreparation(target, validate = false, comment = comment)
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun reviewPreparation(
        target: PreparationReviewDto,
        validate: Boolean,
        comment: String?
    ) {
        _uiState.update { it.copy(isActionInProgress = true, error = null) }
        val flow = if (validate) {
            schoolRepository.validatePreparation(target.id, comment)
        } else {
            schoolRepository.rejectPreparation(target.id, comment)
        }
        flow.onEach { resource ->
            when (resource) {
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    val updated = resource.data
                    _uiState.update { state ->
                        if (updated == null) {
                            state.copy(
                                isActionInProgress = false,
                                showRejectDialog = false,
                                rejectComment = "",
                                error = "Réponse invalide du serveur."
                            )
                        } else {
                            state.copy(
                                preparations = state.preparations.map {
                                    if (it.id == updated.id) updated else it
                                },
                                selectedPreparation = updated,
                                isActionInProgress = false,
                                showRejectDialog = false,
                                rejectComment = ""
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isActionInProgress = false, error = resource.message) }
                }
            }
        }.launchIn(viewModelScope)
    }
}
