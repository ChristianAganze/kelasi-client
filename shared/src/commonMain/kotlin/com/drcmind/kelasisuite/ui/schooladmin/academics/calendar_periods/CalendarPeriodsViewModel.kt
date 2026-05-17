package com.drcmind.kelasisuite.ui.schooladmin.academics.calendar_periods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.domain.dto.AcademicYearDTO
import com.drcmind.kelasisuite.domain.dto.EvaluationPeriodBySchoolDTO
import com.drcmind.kelasisuite.domain.dto.MajorDto
import com.drcmind.kelasisuite.domain.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*

class CalendarPeriodsViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    val uiState : StateFlow<CalendarPeriodsUiState>
        field = MutableStateFlow(CalendarPeriodsUiState())

    init {
        getActiveAcademicYear()
        loadEvaluationPeriods()
        loadActiveAcademicYear()
        loadMajors()
        loadSchoolSections()
    }

    private fun getActiveAcademicYear(){
        val loadActiveAcademicYear = schoolRepository.getActiveAcademicYear()
        uiState.update { it.copy(activeAcademicYear = loadActiveAcademicYear) }
    }

    private fun loadActiveAcademicYear(){
        schoolRepository.getAcademicYears().onEach { ressource ->
            when (ressource) {
                is Resource.Error<*> -> {
                    uiState.update { it.copy(isLoading = false, error = ressource.message) }
                }
                is Resource.Loading<*> -> {
                    uiState.update { it.copy(isLoading = true) }
                }
                is Resource.Success<*> -> {
                    uiState.update { it.copy(academicYears = ressource.data ?: emptyList()) }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun loadMajors(){
        schoolRepository.getOfferedMajorsForSchool().onEach { ressource ->
            when(ressource){
                is Resource.Error<*> -> {
                    uiState.update { it.copy(isLoading = false, error = ressource.message) }
                }
                is Resource.Loading<*> -> {
                    uiState.update { it.copy(isLoading = true) }
                }
                is Resource.Success<*> -> {
                    uiState.update { it.copy(majors = ressource.data ?: emptyList()) }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun loadSchoolSections(){
        schoolRepository.getSchoolSections().onEach { ressource ->
            when(ressource){
                is Resource.Error<*> -> {
                    uiState.update { it.copy(isLoading = false, error = ressource.message) }
                }
                is Resource.Loading<*> -> {
                    uiState.update { it.copy(isLoading = true) }
                }
                is Resource.Success<*> -> {
                    uiState.update { it.copy(schoolSections = ressource.data ?: emptyList()) }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun loadEvaluationPeriods(){
        schoolRepository.getEvaluationPeriodsBySchool().onEach { ressource->
            when(ressource){
                is Resource.Error<*> -> {
                    uiState.update { it.copy(isLoading = false, error = ressource.message) }
                }
                is Resource.Loading<*> -> {
                    uiState.update { it.copy(isLoading = true) }
                }
                is Resource.Success<*> -> {
                    uiState.update { it.copy(evaluationPeriods = ressource.data ?: emptyList()) }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }



}

data class CalendarPeriodsUiState(
    val activeAcademicYear : AcademicYearDTO? = null,
    val academicYears : List<AcademicYearDTO> = emptyList(),
    val majors : List<MajorDto> = emptyList(),
    val schoolSections : List<SchoolSectionDTO> = emptyList(),
    val evaluationPeriods : List<EvaluationPeriodBySchoolDTO> = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null,
)