package com.drcmind.kelasisuite.ui.teacheradmin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.ScheduleEntryDto
import com.drcmind.kelasisuite.data.repository.communication.CommunicationRepository
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.teacher.ClassLogRepository
import com.drcmind.kelasisuite.data.repository.teacher.PreparationRepository
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.domain.util.Resource
import com.drcmind.kelasisuite.domain.util.currentSchoolWeekNumber
import com.drcmind.kelasisuite.domain.util.toFrench
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class DashboardAlert(
    val title: String,
    val message: String
)

data class TeacherDashboardState(
    val username: String = "Professeur",
    val dateLabel: String = "",
    val weekNumber: Int = 0,
    val hasNextClass: Boolean = false,
    val nextClass: String = "",
    val nextClassTime: String = "",
    val todayClassesCount: Int = 0,
    val todaySchedule: List<ScheduleEntryDto> = emptyList(),
    val upcomingThisWeek: List<ScheduleEntryDto> = emptyList(),
    val pendingClassLogs: Int = 0,
    val pendingEvaluations: Int = 0,
    val pendingPreparations: Int = 0,
    val approvedPreparations: Int = 0,
    val rejectedPreparations: Int = 0,
    val unreadMessages: Int = 0,
    val alerts: List<DashboardAlert> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TeacherDashboardViewModel(
    private val settingsStorage: SettingsStorage,
    private val teachersRepository: TeachersRepository,
    private val assignmentRepository: AssignmentRepository,
    private val schoolRepository: SchoolRepository,
    private val classLogRepository: ClassLogRepository,
    private val preparationRepository: PreparationRepository,
    private val communicationRepository: CommunicationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(
        TeacherDashboardState(
            username = settingsStorage.getUserInfo().username?.ifEmpty { "Professeur" } ?: "Professeur",
            dateLabel = buildDateLabel(),
            weekNumber = currentSchoolWeekNumber()
        )
    )
    val state: StateFlow<TeacherDashboardState> = _state.asStateFlow()

    init {
        fetchDashboardData()
    }

    fun retry() {
        _state.update { it.copy(errorMessage = null, isLoading = true) }
        fetchDashboardData()
    }

    private fun buildDateLabel(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return "${now.dayOfWeek.toFrench()} ${now.day} ${now.month.toFrench()} ${now.year}"
    }

    private fun fetchDashboardData() {
        val schoolId = settingsStorage.getSchool()?.id
        val userId = settingsStorage.getUserInfo().userId
        if (schoolId == null || userId == null) {
            _state.update {
                it.copy(isLoading = false, errorMessage = "Connexion incomplète : impossible de charger le tableau de bord.")
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            teachersRepository.getTeachers(schoolId).collect { teachersResource ->
                if (teachersResource is Resource.Success) {
                    val myProfile = teachersResource.data?.find { it.userId == userId }
                    if (myProfile != null) {
                        fetchDashboardSummary(myProfile.id, userId)
                    } else {
                        _state.update { it.copy(isLoading = false, errorMessage = "Profil enseignant introuvable.") }
                    }
                } else if (teachersResource is Resource.Error) {
                    _state.update { it.copy(isLoading = false, errorMessage = teachersResource.message) }
                }
            }
        }
    }

    private suspend fun fetchDashboardSummary(teacherProfileId: Long, userId: Long) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dayOfWeek = now.dayOfWeek
        val currentTime = LocalTime(now.hour, now.minute)
        val todayDate = now.date.toString()
        val currentWeek = currentSchoolWeekNumber()

        assignmentRepository.getAssignmentsForSchool().collect { assignmentsResource ->
            if (assignmentsResource is Resource.Success) {
                val myAssignments = assignmentsResource.data?.filter { it.teacherId == teacherProfileId } ?: emptyList()

                val weekEntries = mutableListOf<ScheduleEntryDto>()
                val todayEntries = mutableListOf<ScheduleEntryDto>()
                var pendingPreps = 0
                var approvedPreps = 0
                var rejectedPreps = 0

                for (assignment in myAssignments) {
                    schoolRepository.getScheduleEntriesByTeachingAssignmentIdAndWeekNumber(assignment.id, currentWeek).collect { schedRes ->
                        if (schedRes is Resource.Success) {
                            schedRes.data?.forEach { entry ->
                                weekEntries.add(entry)
                                if (entry.dayOfWeek == dayOfWeek) todayEntries.add(entry)
                            }
                        }
                    }

                    preparationRepository.getPreparations(assignment.id).collect { prepRes ->
                        if (prepRes is Resource.Success) {
                            prepRes.data?.forEach { dto ->
                                when (dto.status.uppercase()) {
                                    "SUBMITTED", "READY" -> pendingPreps++
                                    "APPROVED" -> approvedPreps++
                                    "REJECTED" -> rejectedPreps++
                                }
                            }
                        }
                    }
                }
                weekEntries.sortBy { it.dayOfWeek.ordinal * 1440 + it.startDayHourTime.hour * 60 + it.startDayHourTime.minute }
                todayEntries.sortBy { it.startDayHourTime }

                val upcoming = todayEntries.firstOrNull { it.endDayHourTime > currentTime }
                val nextClassName = upcoming?.let { "${it.subjectName} (${it.schoolClassName})" }
                    ?: "Aucun cours prévu aujourd'hui"
                val nextTimeStr = when {
                    upcoming == null -> ""
                    upcoming.startDayHourTime <= currentTime -> "En cours (jusqu'à ${upcoming.endDayHourTime})"
                    else -> "À ${upcoming.startDayHourTime}"
                }

                val pastClasses = todayEntries.count { it.endDayHourTime <= currentTime }
                var loggedToday = 0
                for (assignment in myAssignments) {
                    classLogRepository.getClassLogs(assignment.id).collect { logRes ->
                        if (logRes is Resource.Success) {
                            loggedToday += logRes.data?.count { it.date.take(10) == todayDate } ?: 0
                        }
                    }
                }

                var unreadMessages = 0
                communicationRepository.getConversations(userId).collect { convRes ->
                    if (convRes is Resource.Success) {
                        unreadMessages = convRes.data?.sumOf { it.unreadCount } ?: 0
                    }
                }

                val upcomingThisWeek = weekEntries
                    .filter {
                        (it.dayOfWeek.ordinal > dayOfWeek.ordinal) ||
                            (it.dayOfWeek == dayOfWeek && it.endDayHourTime > currentTime)
                    }
                    .take(6)

                val pendingClassLogs = (pastClasses - loggedToday).coerceAtLeast(0)
                val pendingEvaluations = pastClasses

                val alerts = buildList {
                    if (pendingClassLogs > 0) {
                        add(DashboardAlert("Journaux à saisir", "$pendingClassLogs journal(x) de classe à compléter aujourd'hui."))
                    }
                    if (pendingPreps > 0) {
                        add(DashboardAlert("Préparations en attente", "$pendingPreps fiche(s) de préparation en attente de validation."))
                    }
                    if (rejectedPreps > 0) {
                        add(DashboardAlert("Préparations rejetées", "$rejectedPreps fiche(s) rejetée(s) à corriger et resoumettre."))
                    }
                    if (pendingEvaluations > 0) {
                        add(DashboardAlert("Cotes à saisir", "$pendingEvaluations cours du jour sans cotes saisies."))
                    }
                    if (unreadMessages > 0) {
                        add(DashboardAlert("Messages non lus", "Vous avez $unreadMessages message(s) non lu(s)."))
                    }
                    if (isEmpty()) {
                        add(DashboardAlert("Tout est à jour", "Aucune action requise. Bonne journée !"))
                    }
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        hasNextClass = upcoming != null,
                        nextClass = nextClassName,
                        nextClassTime = nextTimeStr,
                        todayClassesCount = todayEntries.size,
                        todaySchedule = todayEntries,
                        upcomingThisWeek = upcomingThisWeek,
                        pendingClassLogs = pendingClassLogs,
                        pendingEvaluations = pendingEvaluations,
                        pendingPreparations = pendingPreps,
                        approvedPreparations = approvedPreps,
                        rejectedPreparations = rejectedPreps,
                        unreadMessages = unreadMessages,
                        alerts = alerts
                    )
                }
            } else if (assignmentsResource is Resource.Error) {
                _state.update { it.copy(isLoading = false, errorMessage = assignmentsResource.message) }
            }
        }
    }
}
