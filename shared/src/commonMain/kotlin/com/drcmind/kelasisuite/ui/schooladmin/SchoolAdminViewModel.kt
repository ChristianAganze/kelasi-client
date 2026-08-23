package com.drcmind.kelasisuite.ui.schooladmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.remote.dto.AcademicYearDTO
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SchoolAdminViewModel(
    private val schoolRepository: SchoolRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SchoolAdminState(activeAcademicYear = schoolRepository.getActiveAcademicYear()))
    val uiState: StateFlow<SchoolAdminState> = _uiState.asStateFlow()

    init {
        loadAcademicYears()
    }
    private fun loadAcademicYears(){
        schoolRepository.getAcademicYears().onEach { ressource ->
            when (ressource) {
                is Resource.Error<*> -> {
                    _uiState.update { it.copy(isLoading = false, error = ressource.message) }
                }
                is Resource.Loading<*> -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
                is Resource.Success<*> -> {
                    _uiState.update { it.copy(academicYears = ressource.data ?: emptyList()) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun selectAcademicYear(academicYear: AcademicYearDTO){
        _uiState.update { it.copy(activeAcademicYear = academicYear) }
        schoolRepository.saveActiveAcademicYearLocally(academicYear)
    }
}

data class SchoolAdminState(
    val academicYears : List<AcademicYearDTO> = emptyList(),
    val activeAcademicYear : AcademicYearDTO? = null,
    val isLoading : Boolean = false,
    val error : String? = null
    )