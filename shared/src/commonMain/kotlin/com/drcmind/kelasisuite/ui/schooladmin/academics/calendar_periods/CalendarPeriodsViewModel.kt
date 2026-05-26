package com.drcmind.kelasisuite.ui.schooladmin.academics.calendar_periods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.datasource.remote.dto.AcademicYearDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodBySchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.MajorDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek

class CalendarPeriodsViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    val uiState: StateFlow<CalendarPeriodsUiState>
        field = MutableStateFlow(CalendarPeriodsUiState())

    init {
        getActiveAcademicYear()
        loadEvaluationPeriods()
        loadActiveAcademicYear()
        loadMajors()
        loadSchoolSections()
//        loadLearningTimeConfigsBySchoolSectionConfigId(1)
    }

    private fun getActiveAcademicYear() {
        val loadActiveAcademicYear = schoolRepository.getActiveAcademicYear()
        uiState.update { it.copy(activeAcademicYear = loadActiveAcademicYear) }
    }

    private fun loadActiveAcademicYear() {
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

    private fun loadMajors() {
        schoolRepository.getOfferedMajorsForSchool().onEach { ressource ->
            when (ressource) {
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

    private fun loadSchoolSections() {
        schoolRepository.getSchoolSections().onEach { ressource ->
            when (ressource) {
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

    private fun loadEvaluationPeriods() {
        schoolRepository.getEvaluationPeriodsBySchool().onEach { ressource ->
            when (ressource) {
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

    fun loadLearningTimeConfigsBySchoolSectionConfigId(schoolSectionConfigId: Long) {
        schoolRepository.getLearningTimeConfigsBySchoolSectionConfigId(schoolSectionConfigId).onEach { ressource ->
            when (ressource) {
                is Resource.Error<*> -> {
                    uiState.update { it.copy(isLoading = false, error = ressource.message) }
                }

                is Resource.Loading<*> -> {
                    uiState.update { it.copy(isLoading = true) }
                }

                is Resource.Success<*> -> {
                    uiState.update { it.copy(learningTimeConfigs = ressource.data ?: emptyList()) }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun loadLearningTimeConfigById(id: Long) {
        schoolRepository.getLearningTimeConfigById(id).onEach { resource ->
            when (resource) {
                is Resource.Error<*> -> {
                    uiState.update { it.copy(isLoading = false, error = resource.message) }
                }

                is Resource.Loading<*> -> {
                    uiState.update { it.copy(isLoading = true) }
                }

                is Resource.Success<*> -> {
                    // Handle single config if needed, or just update loading state
                    uiState.update { it.copy(isLoading = false, error = null) }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun getAllLearningTimeConfigs() {
        schoolRepository.getAllLearningTimeConfigs().onEach { resource ->
            when (resource) {
                is Resource.Error<*> -> {
                    uiState.update { it.copy(isLoading = false, error = resource.message) }
                }

                is Resource.Loading<*> -> {
                    uiState.update { it.copy(isLoading = true) }
                }

                is Resource.Success<*> -> {
                    uiState.update {
                        it.copy(
                            learningTimeConfigs = resource.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun loadLearningTimeConfigsByDayOfWeekAndSchoolSectionConfigId(dayOfWeek: DayOfWeek, schoolSectionConfigId: Long) {
        schoolRepository.getLearningTimeConfigsByDayOfWeekAndSchoolSectionConfigId(dayOfWeek, schoolSectionConfigId)
            .onEach { resource ->
                when (resource) {
                    is Resource.Error<*> -> {
                        uiState.update { it.copy(isLoading = false, error = resource.message) }
                    }

                    is Resource.Loading<*> -> {
                        uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success<*> -> {
                        uiState.update {
                            it.copy(
                                learningTimeConfigs = resource.data ?: emptyList(),
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
    }

    fun createLearningTimeConfig(configDto: LearningTimeConfigDto) {
        viewModelScope.launch {
            schoolRepository.createLearningTimeConfig(configDto).onEach { resource ->
                when (resource) {
                    is Resource.Error<*> -> {
                        uiState.update { it.copy(isLoading = false, error = resource.message) }
                    }

                    is Resource.Loading<*> -> {
                        uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success<*> -> {
                        // Reload learning time configs after successful creation
                        configDto.schoolSectionConfigId?.let {
                            loadLearningTimeConfigsBySchoolSectionConfigId(it)
                        }
                        uiState.update { it.copy(isLoading = false, error = null) }
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateLearningTimeConfig(id: Long, configDto: LearningTimeConfigDto) {
        viewModelScope.launch {
            schoolRepository.updateLearningTimeConfig(id, configDto).onEach { resource ->
                when (resource) {
                    is Resource.Error<*> -> {
                        uiState.update { it.copy(isLoading = false, error = resource.message) }
                    }

                    is Resource.Loading<*> -> {
                        uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success<*> -> {
                        // Reload learning time configs after successful update
                        configDto.schoolSectionConfigId?.let {
                            loadLearningTimeConfigsBySchoolSectionConfigId(it)
                        }
                        uiState.update { it.copy(isLoading = false, error = null) }
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deleteLearningTimeConfig(id: Long) {
        viewModelScope.launch {
            // Find the schoolSectionConfigId before deletion to reload correctly
            val schoolSectionConfigIdToReload =
                uiState.value.learningTimeConfigs.firstOrNull { it.id == id }?.schoolSectionConfigId

            schoolRepository.deleteLearningTimeConfig(id).onEach { resource ->
                when (resource) {
                    is Resource.Error<*> -> {
                        uiState.update { it.copy(isLoading = false, error = resource.message) }
                    }

                    is Resource.Loading<*> -> {
                        uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success<*> -> {
                        // Reload learning time configs after successful deletion
                        schoolSectionConfigIdToReload?.let {
                            loadLearningTimeConfigsBySchoolSectionConfigId(it)
                        }
                        uiState.update { it.copy(isLoading = false, error = null) }
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }
}

data class CalendarPeriodsUiState(
    val activeAcademicYear: AcademicYearDTO? = null,
    val academicYears: List<AcademicYearDTO> = emptyList(),
    val majors: List<MajorDto> = emptyList(),
    val schoolSections: List<SchoolSectionDTO> = emptyList(),
    val evaluationPeriods: List<EvaluationPeriodBySchoolDTO> = emptyList(),
    val learningTimeConfigs: List<LearningTimeConfigDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)