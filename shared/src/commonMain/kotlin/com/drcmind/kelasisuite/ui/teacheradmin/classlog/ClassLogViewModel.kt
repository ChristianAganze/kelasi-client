package com.drcmind.kelasisuite.ui.teacheradmin.classlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.ClassLogDTO
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.data.repository.teacher.ClassLogRepository
import com.drcmind.kelasisuite.data.repository.teacher.PreparationRepository
import com.drcmind.kelasisuite.data.repository.teacher.toLessonPreparation
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.domain.model.teacher.ClassLogEntry
import com.drcmind.kelasisuite.domain.model.teacher.LessonPreparation
import com.drcmind.kelasisuite.domain.model.teacher.LogStatus
import com.drcmind.kelasisuite.domain.model.teacher.StudentEval
import com.drcmind.kelasisuite.domain.util.Resource
import com.drcmind.kelasisuite.domain.util.currentSchoolWeekNumber
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
    val saveError: String? = null,
    val selectedEntryId: String? = null,
    val showStatusDialog: Boolean = false,
    val showLinkDialog: Boolean = false,
    val showPresenceDialog: Boolean = false,
    val presenceStudents: List<StudentEval> = emptyList(),
    val presenceStudentIds: Set<Long> = emptySet(),
    val showSignatureDialog: Boolean = false,
    val signingEntryId: String? = null
)

class ClassLogViewModel(
    private val settingsStorage: SettingsStorage,
    private val teachersRepository: TeachersRepository,
    private val assignmentRepository: AssignmentRepository,
    private val schoolRepository: SchoolRepository,
    private val classLogRepository: ClassLogRepository,
    private val preparationRepository: PreparationRepository,
    private val studentsRepository: StudentsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ClassLogState())
    val state: StateFlow<ClassLogState> = _state.asStateFlow()

    init {
        fetchTodaySchedule()
    }

    fun retry() {
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
        val currentWeek = currentSchoolWeekNumber()

        assignmentRepository.getAssignmentsForSchool().collect { assignmentsResource ->
            if (assignmentsResource is Resource.Success) {
                val myAssignments = assignmentsResource.data?.filter { it.teacherId == teacherProfileId } ?: emptyList()
                val entries = mutableListOf<ClassLogEntry>()
                val preparations = mutableListOf<LessonPreparation>()

                for (assignment in myAssignments) {
                    // Collect preparations to link (best-effort, ignore errors per assignment)
                    preparationRepository.getPreparations(assignment.id).collect { prepRes ->
                        if (prepRes is Resource.Success) {
                            prepRes.data?.forEach { dto ->
                                preparations.add(
                                    dto.toLessonPreparation(
                                        branch = assignment.subjectName,
                                        className = assignment.className
                                    )
                                )
                            }
                        }
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
                                        teachingAssignmentId = schedDto.teachingAssignmentId,
                                        scheduleEntryId = schedDto.id,
                                        classId = assignment.classId,
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
                        scheduleToday = entries.sortedBy { e -> e.timeSlot },
                        availablePreparations = preparations
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
        _state.update {
            it.copy(
                selectedEntryId = null,
                showStatusDialog = false,
                showLinkDialog = false,
                showPresenceDialog = false,
                presenceStudents = emptyList(),
                presenceStudentIds = emptySet()
            )
        }
    }

    fun dismissSnackbar() {
        _state.update { it.copy(saveSuccess = false, saveError = null) }
    }

    fun selectEntryForPresence(id: String) {
        val entry = _state.value.scheduleToday.find { it.id == id } ?: return
        val classId = entry.classId ?: return
        _state.update { it.copy(selectedEntryId = id, showPresenceDialog = true) }
        viewModelScope.launch {
            studentsRepository.getStudentsForClass(classId).collect { res ->
                if (res is Resource.Success) {
                    val students = (res.data ?: emptyList()).map { dto ->
                        StudentEval(
                            id = dto.id.toString(),
                            firstName = dto.firstName,
                            lastName = dto.lastName
                        )
                    }
                    val present = entry.presentStudentIds
                    _state.update {
                        it.copy(
                            presenceStudents = students,
                            presenceStudentIds = present.ifEmpty { students.map { s -> s.id.toLong() }.toSet() }
                        )
                    }
                }
            }
        }
    }

    fun togglePresence(studentId: Long) {
        _state.update { state ->
            val ids = if (studentId in state.presenceStudentIds) {
                state.presenceStudentIds - studentId
            } else {
                state.presenceStudentIds + studentId
            }
            state.copy(presenceStudentIds = ids)
        }
    }

    fun confirmPresence() {
        val entryId = _state.value.selectedEntryId ?: return
        val present = _state.value.presenceStudentIds
        _state.update { state ->
            val updatedSchedule = state.scheduleToday.map {
                if (it.id == entryId) it.copy(presentStudentIds = present) else it
            }
            state.copy(
                scheduleToday = updatedSchedule,
                showPresenceDialog = false,
                selectedEntryId = null,
                presenceStudents = emptyList(),
                presenceStudentIds = emptySet()
            )
        }
    }

    fun submitEntry(id: String) {
        openSignatureDialog(id)
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

    fun openSignatureDialog(entryId: String) {
        _state.update { it.copy(showSignatureDialog = true, signingEntryId = entryId) }
    }

    fun closeSignatureDialog() {
        _state.update { it.copy(showSignatureDialog = false, signingEntryId = null) }
    }

    fun applySignature(signature: com.drcmind.kelasisuite.domain.model.common.ElectronicSignature) {
        val entryId = _state.value.signingEntryId ?: return
        _state.update { state ->
            val updatedSchedule = state.scheduleToday.map {
                if (it.id == entryId) it.copy(
                    status = LogStatus.COMPLETED,
                    submitted = true,
                    teacherSignature = signature
                ) else it
            }
            state.copy(
                scheduleToday = updatedSchedule,
                showSignatureDialog = false,
                signingEntryId = null,
                saveSuccess = true
            )
        }
        saveClassLogs()
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
                    val teachingAssignmentId = entry.teachingAssignmentId
                    if (teachingAssignmentId == null) continue
                    val dto = ClassLogDTO(
                        teachingAssignmentId = teachingAssignmentId,
                        scheduleEntryId = entry.scheduleEntryId,
                        date = currentDate,
                        taughtSubject = entry.subject,
                        homework = entry.homework,
                        teacherSignature = true,
                        adminSignature = entry.submitted,
                        presentStudentIds = entry.presentStudentIds.toList()
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
                    saveError = if (hasError) "Certains journaux n'ont pas pu être sauvegardés." else null
                ) 
            }
        }
    }
}
