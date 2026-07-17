package com.drcmind.kelasisuite.ui.teacheradmin.classlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.ClassLogDTO
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.teacher.ClassLogRepository
import com.drcmind.kelasisuite.data.repository.teacher.PreparationRepository
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.domain.model.teacher.ClassLogEntry
import com.drcmind.kelasisuite.domain.model.teacher.LessonPreparation
import com.drcmind.kelasisuite.domain.model.teacher.LogStatus
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ClassLogState(
    val scheduleToday: List<ClassLogEntry> = emptyList(),
    val availablePreparations: List<LessonPreparation> = emptyList(), 
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val selectedEntryId: String? = null,
    val showStatusDialog: Boolean = false,
    val showLinkDialog: Boolean = false
)

class ClassLogViewModel(
    private val settingsStorage: SettingsStorage,
    private val teachersRepository: TeachersRepository,
    private val assignmentRepository: AssignmentRepository,
    private val schoolRepository: SchoolRepository,
    private val classLogRepository: ClassLogRepository,
    private val preparationRepository: PreparationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ClassLogState())
    val state: StateFlow<ClassLogState> = _state.asStateFlow()

    init {
        fetchTodaySchedule()
    }

    private fun fetchTodaySchedule() {
        val schoolId = settingsStorage.getSchool()?.id ?: return
        val userId = settingsStorage.getUserInfo().userId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            // 1. Get Teacher Profile
            teachersRepository.getTeachers(schoolId).collect { teachersResource ->
                if (teachersResource is Resource.Success) {
                    val myProfile = teachersResource.data?.find { it.userId == userId }
                    if (myProfile != null) {
                        fetchAssignmentsAndSchedule(myProfile.id)
                    } else {
                        _state.update { it.copy(isLoading = false, errorMessage = "Profil enseignant introuvable.") }
                    }
                } else if (teachersResource is Resource.Error) {
                    _state.update { it.copy(isLoading = false, errorMessage = teachersResource.message) }
                }
            }
        }
    }

    private suspend fun fetchAssignmentsAndSchedule(teacherProfileId: Long) {
        // Obtenir la date d'aujourd'hui
        // Get today's date
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dayOfWeek = today.dayOfWeek
        val currentWeek = 1 // Mocking week 1 for MVP

        assignmentRepository.getAssignmentsForSchool().collect { assignmentsResource ->
            if (assignmentsResource is Resource.Success) {
                val myAssignments = assignmentsResource.data?.filter { it.teacherId == teacherProfileId } ?: emptyList()
                val entries = mutableListOf<ClassLogEntry>()
                
                for (assignment in myAssignments) {
                    // Fetch preparations to link
                    preparationRepository.getPreparations(assignment.id).collect {
                        // In a real app, we'd map and add these to availablePreparations
                    }

                    // Fetch schedule
                    schoolRepository.getScheduleEntriesByTeachingAssignmentIdAndWeekNumber(assignment.id, currentWeek).collect { schedRes ->
                        if (schedRes is Resource.Success) {
                            val todaySchedule = schedRes.data?.filter { it.dayOfWeek == dayOfWeek } ?: emptyList()
                            todaySchedule.forEach { schedDto ->
                                entries.add(
                                    ClassLogEntry(
                                        id = schedDto.id.toString(),
                                        timeSlot = "${schedDto.startDayHourTime} - ${schedDto.endDayHourTime}",
                                        className = schedDto.schoolClassName,
                                        subject = schedDto.subjectName,
                                        status = LogStatus.NOT_STARTED,
                                        teacherNote = ""
                                    )
                                )
                            }
                        }
                    }
                }
                
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        scheduleToday = entries.sortedBy { e -> e.timeSlot }
                    ) 
                }
            } else if (assignmentsResource is Resource.Error) {
                _state.update { it.copy(isLoading = false, errorMessage = assignmentsResource.message) }
            }
        }
    }

    fun selectEntryForStatusUpdate(id: String) {
        _state.update { it.copy(selectedEntryId = id, showStatusDialog = true) }
    }

    fun selectEntryForLinking(id: String) {
        _state.update { it.copy(selectedEntryId = id, showLinkDialog = true) }
    }

    fun dismissDialogs() {
        _state.update { it.copy(selectedEntryId = null, showStatusDialog = false, showLinkDialog = false) }
    }

    fun dismissSnackbar() {
        _state.update { it.copy(saveSuccess = false, errorMessage = null) }
    }

    fun updateStatus(status: LogStatus, note: String, homework: String) {
        val id = _state.value.selectedEntryId ?: return
        _state.update { state ->
            val updatedSchedule = state.scheduleToday.map {
                if (it.id == id) it.copy(status = status, teacherNote = note, homework = homework) else it
            }
            state.copy(scheduleToday = updatedSchedule, showStatusDialog = false, selectedEntryId = null)
        }
    }

    fun linkPreparation(prepId: String) {
        val entryId = _state.value.selectedEntryId ?: return
        val prep = _state.value.availablePreparations.find { it.id == prepId } ?: return
        
        _state.update { state ->
            val updatedSchedule = state.scheduleToday.map {
                if (it.id == entryId) it.copy(
                    linkedPreparationId = prep.id, 
                    linkedPreparationTitle = prep.header.lessonSubject,
                    linkedObjective = prep.header.operationalObjective,
                    linkedReference = prep.header.bibliography
                ) else it
            }
            state.copy(scheduleToday = updatedSchedule, showLinkDialog = false, selectedEntryId = null)
        }
    }

    fun saveClassLogs() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            
            // Obtenir la date actuelle sous forme de chaîne
            // Get the current date as a string
            val currentDate = Clock.System.now().toString()
            var hasError = false
            
            for (entry in _state.value.scheduleToday) {
                if (entry.status == LogStatus.COMPLETED) {
                    val dto = ClassLogDTO(
                        teachingAssignmentId = entry.id.toLongOrNull() ?: 0L, // Should map correctly in real app
                        date = currentDate,
                        taughtSubject = entry.subject,
                        homework = entry.homework,
                        teacherSignature = true,
                        adminSignature = false
                    )
                    classLogRepository.createClassLog(dto).collect { res ->
                        if (res is Resource.Error) hasError = true
                    }
                }
            }
            
            _state.update { 
                it.copy(
                    isSaving = false, 
                    saveSuccess = !hasError,
                    errorMessage = if (hasError) "Certains journaux n'ont pas pu être sauvegardés." else null
                ) 
            }
        }
    }
}
