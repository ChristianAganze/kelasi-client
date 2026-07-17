package com.drcmind.kelasisuite.ui.teacheradmin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.ScheduleEntryDto
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.teacher.ClassLogRepository
import com.drcmind.kelasisuite.data.repository.teacher.EvaluationRepository
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class TeacherDashboardState(
    val username: String = "Professeur",
    val nextClass: String = "Recherche en cours...",
    val nextClassTime: String = "",
    val pendingClassLogs: Int = 0,
    val pendingEvaluations: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TeacherDashboardViewModel(
    private val settingsStorage: SettingsStorage,
    private val teachersRepository: TeachersRepository,
    private val assignmentRepository: AssignmentRepository,
    private val schoolRepository: SchoolRepository,
    private val classLogRepository: ClassLogRepository,
    private val evaluationRepository: EvaluationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(
        TeacherDashboardState(
            username = settingsStorage.getUserInfo().username?.ifEmpty { "Professeur" } ?: "Professeur"
        )
    )
    val state: StateFlow<TeacherDashboardState> = _state.asStateFlow()

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        val schoolId = settingsStorage.getSchool()?.id ?: return
        val userId = settingsStorage.getUserInfo().userId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            teachersRepository.getTeachers(schoolId).collect { teachersResource ->
                if (teachersResource is Resource.Success) {
                    val myProfile = teachersResource.data?.find { it.userId == userId }
                    if (myProfile != null) {
                        fetchNextClass(myProfile.id)
                    } else {
                        _state.update { it.copy(isLoading = false, errorMessage = "Profil enseignant introuvable.") }
                    }
                } else if (teachersResource is Resource.Error) {
                    _state.update { it.copy(isLoading = false, errorMessage = teachersResource.message) }
                }
            }
        }
    }

    private suspend fun fetchNextClass(teacherProfileId: Long) {
        // Obtenir l'heure actuelle
        // Get the current time
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dayOfWeek = now.dayOfWeek
        val currentTime = LocalTime(now.hour, now.minute)
        val currentWeek = 1 // Mocking week 1 for MVP

        assignmentRepository.getAssignmentsForSchool().collect { assignmentsResource ->
            if (assignmentsResource is Resource.Success) {
                val myAssignments = assignmentsResource.data?.filter { it.teacherId == teacherProfileId } ?: emptyList()

                var nextClassName = "Aucun cours prévu aujourd'hui"
                var nextTimeStr = ""

                var foundNext = false
                
                for (assignment in myAssignments) {
                    if (foundNext) break
                    
                    schoolRepository.getScheduleEntriesByTeachingAssignmentIdAndWeekNumber(assignment.id, currentWeek).collect { schedRes ->
                        if (schedRes is Resource.Success && !foundNext) {
                            val todaySchedule: List<ScheduleEntryDto> = schedRes.data?.filter { it.dayOfWeek == dayOfWeek } ?: emptyList()
                            
                            // Find the first class that hasn't ended yet
                            val upcoming = todaySchedule.sortedBy { it.startDayHourTime }.firstOrNull { 
                                it.endDayHourTime > currentTime 
                            }
                            
                            if (upcoming != null) {
                                nextClassName = "${upcoming.subjectName} (${upcoming.schoolClassName})"
                                
                                val isHappeningNow = upcoming.startDayHourTime <= currentTime && upcoming.endDayHourTime >= currentTime
                                nextTimeStr = if (isHappeningNow) {
                                    "En cours (jusqu'à ${upcoming.endDayHourTime})"
                                } else {
                                    "À ${upcoming.startDayHourTime}"
                                }
                                foundNext = true
                            }
                        }
                    }
                }
                
                // We'll simulate fetching logs & evals concurrently or sequentially
                var pendingLogs = 0
                var pendingEvals = 0

                // Quick logic: For every assignment, fetch its logs and evaluations
                // In production, we'd check `scheduleToday` entries up to `currentTime` vs `classLogs` today.
                // To avoid overly complex logic blocking the UI, we'll just fetch all assignments and do a basic check.
                for (assignment in myAssignments) {
                    classLogRepository.getClassLogs(assignment.id).collect { logRes ->
                        if (logRes is Resource.Success) {
                            val logsToday = logRes.data?.filter { 
                                it.date.startsWith(now.date.toString()) 
                            } ?: emptyList()
                            // If teacher has 2 classes today and 1 log, pending is 1.
                            // We can just add a placeholder pending = total past classes - logs
                        }
                    }
                    evaluationRepository.getStudentEvaluations(assignment.id).collect { evRes ->
                        // Similar logic for evaluations
                    }
                }
                
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        nextClass = nextClassName,
                        nextClassTime = nextTimeStr,
                        pendingClassLogs = pendingLogs, // Replace with real diff once class history matches
                        pendingEvaluations = pendingEvals
                    ) 
                }
            } else if (assignmentsResource is Resource.Error) {
                _state.update { it.copy(isLoading = false, errorMessage = assignmentsResource.message) }
            }
        }
    }
}
