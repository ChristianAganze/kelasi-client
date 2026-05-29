package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ScheduleEntryDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiService
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeLevelDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.MajorDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SectionDTO

class ScheduleViewModel(
    private val schoolRepository: SchoolRepository,
    private val settingsStorage: SettingsStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadSchoolSections()
        loadSchoolSectionConfigs()
        _uiState.update { it.copy(currentWeekNumber = getCurrentWeekNumber()) }
    }

    private fun loadSchoolSectionConfigs() {
        schoolRepository.getAllSchoolSectionConfigsBySchool().onEach { resource ->
            if (resource is Resource.Success) {
                _uiState.update { it.copy(schoolSectionConfigs = resource.data ?: emptyList()) }
            }
        }.launchIn(viewModelScope)
    }

    private fun getCurrentWeekNumber(): Int {
        val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return today.dayOfYear / 7 + 1
    }

    private fun loadSchoolSections() {
        schoolRepository.getSchoolSections().onEach { resource ->
            _uiState.update { currentState ->
                when (resource) {
                    is Resource.Loading -> currentState.copy(isLoadingSchoolSections = true)
                    is Resource.Success -> {
                        val schoolSections = resource.data ?: emptyList()
                        val selectedSchoolSection = currentState.selectedSchoolSection ?: schoolSections.firstOrNull()
                        if (selectedSchoolSection != null && currentState.selectedSchoolSection == null) {
                            selectSchoolSection(selectedSchoolSection)
                        }
                        currentState.copy(
                            schoolSections = schoolSections,
                            isLoadingSchoolSections = false,
                            error = null
                        )
                    }
                    is Resource.Error -> currentState.copy(isLoadingSchoolSections = false, error = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun selectSchoolSection(schoolSection: SchoolSectionDTO) {
        _uiState.update {
            it.copy(
                selectedSchoolSection = schoolSection,
                selectedSection = null,
                selectedMajor = null,
                selectedGradeLevel = null,
                selectedClass = null,
                sections = emptyList(),
                majors = emptyList(),
                gradeLevels = emptyList(),
                classes = emptyList(),
                allLearningTimeConfigs = emptyList()
            )
        }
        
        val configId = _uiState.value.schoolSectionConfigs.find { it.schoolSectionId == schoolSection.id }?.id
        if (configId != null) {
            loadLearningTimeConfigs(configId)
        }
        
        loadSections(schoolSection.id)
    }

    private fun loadSections(schoolSectionId: Long) {
        schoolRepository.getSectionBySchoolSectionAndSchool(schoolSectionId).onEach { resource ->
            _uiState.update { currentState ->
                when (resource) {
                    is Resource.Loading -> currentState.copy(isLoadingSections = true)
                    is Resource.Success -> {
                        val sections = resource.data ?: emptyList()
                        val selectedSection = currentState.selectedSection ?: sections.firstOrNull()
                        if (selectedSection != null && currentState.selectedSection == null) {
                            selectSection(selectedSection)
                        }
                        currentState.copy(
                            sections = sections,
                            isLoadingSections = false,
                            error = null
                        )
                    }
                    is Resource.Error -> currentState.copy(isLoadingSections = false, error = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun selectSection(section: SectionDTO) {
        _uiState.update {
            it.copy(
                selectedSection = section,
                selectedMajor = null,
                selectedGradeLevel = null,
                selectedClass = null,
                majors = emptyList(),
                gradeLevels = emptyList(),
                classes = emptyList()
            )
        }
        loadMajors(section.id)
    }

    private fun loadMajors(sectionId: Long) {
        schoolRepository.getOfferedMajorsForSchoolAndSection(sectionId).onEach { resource ->
            _uiState.update { currentState ->
                when (resource) {
                    is Resource.Loading -> currentState.copy(isLoadingMajors = true)
                    is Resource.Success -> {
                        val majors = resource.data ?: emptyList()
                        val selectedMajor = currentState.selectedMajor ?: majors.firstOrNull()
                        if (selectedMajor != null && currentState.selectedMajor == null) {
                            selectMajor(selectedMajor)
                        }
                        currentState.copy(
                            majors = majors,
                            isLoadingMajors = false,
                            error = null
                        )
                    }
                    is Resource.Error -> currentState.copy(isLoadingMajors = false, error = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun selectMajor(major: MajorDto) {
        _uiState.update {
            it.copy(
                selectedMajor = major,
                selectedGradeLevel = null,
                selectedClass = null,
                gradeLevels = emptyList(),
                classes = emptyList()
            )
        }
        loadGradeLevels(major.id)
    }

    private fun loadGradeLevels(majorId: Long) {
        schoolRepository.getGradeLevelsBySchoolAndByMajor(majorId).onEach { resource ->
            _uiState.update { currentState ->
                when (resource) {
                    is Resource.Loading -> currentState.copy(isLoadingGradeLevels = true)
                    is Resource.Success -> {
                        val gradeLevels = resource.data ?: emptyList()
                        val selectedGradeLevel = currentState.selectedGradeLevel ?: gradeLevels.firstOrNull()
                        if (selectedGradeLevel != null && currentState.selectedGradeLevel == null) {
                            selectGradeLevel(selectedGradeLevel)
                        }
                        currentState.copy(
                            gradeLevels = gradeLevels,
                            isLoadingGradeLevels = false,
                            error = null
                        )
                    }
                    is Resource.Error -> currentState.copy(isLoadingGradeLevels = false, error = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun selectGradeLevel(gradeLevel: GradeLevelDTO) {
        _uiState.update {
            it.copy(
                selectedGradeLevel = gradeLevel,
                selectedClass = null,
                classes = emptyList()
            )
        }
        loadClasses(gradeLevel.id)
    }

    private fun loadClasses(gradeLevelId: Long) {
        schoolRepository.getClassesBySchoolAndGradeLevel(gradeLevelId).onEach { resource ->
            _uiState.update { currentState ->
                when (resource) {
                    is Resource.Loading -> currentState.copy(isLoadingClasses = true)
                    is Resource.Success -> {
                        val classes = resource.data ?: emptyList()
                        val selectedClass = currentState.selectedClass ?: classes.firstOrNull()
                        if (selectedClass != null && currentState.selectedClass == null) {
                            selectClass(selectedClass)
                        }
                        currentState.copy(
                            classes = classes,
                            isLoadingClasses = false,
                            selectedClass = selectedClass,
                            error = null
                        )
                    }
                    is Resource.Error -> currentState.copy(isLoadingClasses = false, error = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }


    private fun loadLearningTimeConfigs(schoolSectionId: Long) {
        schoolRepository.getLearningTimeConfigsBySchoolSectionConfigId(schoolSectionId).onEach { configResource ->
            if (configResource is Resource.Success) {
                _uiState.update { it.copy(allLearningTimeConfigs = configResource.data ?: emptyList()) }
            } else if (configResource is Resource.Error) {
                _uiState.update { it.copy(error = configResource.message) }
            }
        }.launchIn(viewModelScope)
    }


    fun selectClass(schoolClass: SchoolClassDTO) {
        _uiState.update { it.copy(selectedClass = schoolClass) }
        loadWeeklySchedule(schoolClass.id, _uiState.value.currentWeekNumber)
        loadAssignments(schoolClass.id)
    }

    fun loadAssignments(classId: Long) {
        val academicYearId = settingsStorage.getActiveAcademicYear()?.id ?: return
        schoolRepository.getAssignmentsForClass(classId, academicYearId).onEach { resource ->
            _uiState.update { currentState ->
                when (resource) {
                    is Resource.Loading -> currentState // or copy(isLoadingAssignments = true)
                    is Resource.Success -> currentState.copy(assignments = resource.data ?: emptyList())
                    is Resource.Error -> currentState.copy(error = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createScheduleEntry(entryDto: ScheduleEntryDto) {
        schoolRepository.createScheduleEntry(entryDto).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.value.selectedClass?.id?.let {
                        loadWeeklySchedule(it, _uiState.value.currentWeekNumber)
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(error = resource.message) }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun updateScheduleEntry(id: Long, entryDto: ScheduleEntryDto) {
        schoolRepository.updateScheduleEntry(id, entryDto).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.value.selectedClass?.id?.let {
                        loadWeeklySchedule(it, _uiState.value.currentWeekNumber)
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(error = resource.message) }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun deleteScheduleEntry(id: Long) {
        schoolRepository.deleteScheduleEntry(id).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.value.selectedClass?.id?.let {
                        loadWeeklySchedule(it, _uiState.value.currentWeekNumber)
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(error = resource.message) }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun clearWeek(classId: Long, weekNumber: Int) {
        schoolRepository.clearWeek(weekNumber, classId).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    loadWeeklySchedule(classId, weekNumber)
                }
                is Resource.Error -> _uiState.update { it.copy(error = resource.message) }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun duplicateWeek(sourceWeek: Int, classId: Long, targetWeeks: List<Int>) {
        schoolRepository.duplicateScheduleEntries(sourceWeek, classId, targetWeeks).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Maybe show success message
                    loadWeeklySchedule(classId, _uiState.value.currentWeekNumber)
                }
                is Resource.Error -> _uiState.update { it.copy(error = resource.message) }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun goToNextWeek() {
        val nextWeek = _uiState.value.currentWeekNumber + 1
        _uiState.update { it.copy(currentWeekNumber = nextWeek) }
        _uiState.value.selectedClass?.id?.let { classId ->
            loadWeeklySchedule(classId, nextWeek)
        }
    }

    fun goToPreviousWeek() {
        val previousWeek = (_uiState.value.currentWeekNumber - 1).coerceAtLeast(1)
        _uiState.update { it.copy(currentWeekNumber = previousWeek) }
        _uiState.value.selectedClass?.id?.let { classId ->
            loadWeeklySchedule(classId, previousWeek)
        }
    }

    fun loadWeeklySchedule(classId: Long, weekNumber: Int) {
        _uiState.update { it.copy(isLoadingSchedule = true, error = null) }

        schoolRepository.getWeeklySchedule(weekNumber, classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _uiState.update { it.copy(isLoadingSchedule = true) }
                is Resource.Success -> {
                    val scheduleEntries = resource.data ?: emptyList()
                    val academicYearId = settingsStorage.getActiveAcademicYear()?.id

                    if (academicYearId == null) {
                        _uiState.update { it.copy(isLoadingSchedule = false, error = "Aucune année académique active.") }
                        return@onEach
                    }

                    viewModelScope.launch {
                        val detailedEntriesDeferred = scheduleEntries.map { entry ->
                            async {
                                val learningTimeConfigDeferred = async {
                                    schoolRepository.getLearningTimeConfigById(entry.learningTimeConfigId).firstSuccessOrNull()
                                }
                                val teachingAssignment = _uiState.value.assignments.find { it.id == entry.teachingAssignmentId }

                                DetailedScheduleEntry(
                                    scheduleEntry = entry,
                                    learningTimeConfig = learningTimeConfigDeferred.await(),
                                    teachingAssignment = teachingAssignment
                                )
                            }
                        }

                        val detailedEntries = detailedEntriesDeferred.awaitAll()

                        _uiState.update {
                            it.copy(
                                schedule = detailedEntries,
                                isLoadingSchedule = false,
                                error = null
                            )
                        }
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(isLoadingSchedule = false, error = resource.message) }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun <T> Flow<Resource<T>>.firstSuccessOrNull(): T? {
        return this.firstOrNull { it is Resource.Success }?.data
    }
}

data class ScheduleUiState(
    val schoolSections: List<SchoolSectionDTO> = emptyList(),
    val selectedSchoolSection: SchoolSectionDTO? = null,
    val isLoadingSchoolSections: Boolean = false,

    val schoolSectionConfigs: List<SchoolSectionConfigDto> = emptyList(),

    val sections: List<SectionDTO> = emptyList(),
    val selectedSection: SectionDTO? = null,
    val isLoadingSections: Boolean = false,

    val majors: List<MajorDto> = emptyList(),
    val selectedMajor: MajorDto? = null,
    val isLoadingMajors: Boolean = false,

    val gradeLevels: List<GradeLevelDTO> = emptyList(),
    val selectedGradeLevel: GradeLevelDTO? = null,
    val isLoadingGradeLevels: Boolean = false,

    val classes: List<SchoolClassDTO> = emptyList(),
    val selectedClass: SchoolClassDTO? = null,
    val isLoadingClasses: Boolean = false,

    val currentWeekNumber: Int = 1,
    val schedule: List<DetailedScheduleEntry> = emptyList(),
    val allLearningTimeConfigs: List<LearningTimeConfigDto> = emptyList(),
    val assignments: List<TeachingAssignmentDTO> = emptyList(),
    val isLoadingSchedule: Boolean = false,
    val error: String? = null
)

data class DetailedScheduleEntry(
    val scheduleEntry: ScheduleEntryDto,
    val learningTimeConfig: LearningTimeConfigDto?,
    val teachingAssignment: TeachingAssignmentDTO?=null
)
