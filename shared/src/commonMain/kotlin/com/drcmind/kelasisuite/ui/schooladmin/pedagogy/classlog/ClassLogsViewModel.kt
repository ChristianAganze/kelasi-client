package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.classlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.remote.dto.ClassLogReviewDto
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

enum class ClassLogFilter {
    ALL,
    UNSIGNED,
    SIGNED
}

data class ClassLogsUiState(
    val logs: List<ClassLogReviewDto> = emptyList(),
    val filter: ClassLogFilter = ClassLogFilter.ALL,
    val selectedLog: ClassLogReviewDto? = null,
    val isActionInProgress: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ClassLogsViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClassLogsUiState())
    val uiState: StateFlow<ClassLogsUiState> = _uiState.asStateFlow()

    init {
        loadClassLogs()
    }

    fun loadClassLogs() {
        schoolRepository.getClassLogsForReview().onEach { resource ->
            _uiState.update { state ->
                when (resource) {
                    is Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> state.copy(
                        logs = resource.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                    is Resource.Error -> state.copy(isLoading = false, error = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setFilter(filter: ClassLogFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun openDetail(log: ClassLogReviewDto) {
        _uiState.update { it.copy(selectedLog = log, error = null) }
    }

    fun dismissDetail() {
        _uiState.update { it.copy(selectedLog = null) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun signClassLog() {
        val target = _uiState.value.selectedLog ?: return
        _uiState.update { it.copy(isActionInProgress = true, error = null) }
        schoolRepository.signClassLog(target.id).onEach { resource ->
            when (resource) {
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    val updated = resource.data
                    _uiState.update { state ->
                        if (updated == null) {
                            state.copy(
                                isActionInProgress = false,
                                error = "Réponse invalide du serveur."
                            )
                        } else {
                            state.copy(
                                logs = state.logs.map {
                                    if (it.id == updated.id) updated else it
                                },
                                selectedLog = updated,
                                isActionInProgress = false
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
