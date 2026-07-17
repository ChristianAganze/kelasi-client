package com.drcmind.kelasisuite.ui.teacheradmin.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.ScheduleEntryDto
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class TeacherScheduleState(
    val isLoading: Boolean = false,
    val scheduleEntries: List<ScheduleEntryDto> = emptyList(),
    val currentWeekNumber: Int = 1,
    val errorMessage: String? = null
)

class TeacherScheduleViewModel(
    private val schoolRepository: SchoolRepository,
    private val assignmentRepository: AssignmentRepository,
    private val teachersRepository: TeachersRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(TeacherScheduleState())
    val state: StateFlow<TeacherScheduleState> = _state.asStateFlow()

    init {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        // Calcul simple pour le numéro de semaine de l'année
        // Simple calculation for the week number of the year
        val currentWeek = now.dayOfYear / 7 + 1
        _state.update { it.copy(currentWeekNumber = currentWeek) }

        loadTeacherSchedule(currentWeek)
    }

    private fun loadTeacherSchedule(weekNumber: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val schoolId = settingsStorage.getSchool()?.id ?: run {
                _state.update { it.copy(isLoading = false, errorMessage = "École introuvable / School not found") }
                return@launch
            }
            val userId = settingsStorage.getUserInfo().userId ?: run {
                _state.update { it.copy(isLoading = false, errorMessage = "Utilisateur introuvable / User not found") }
                return@launch
            }

            try {
                // Fetch the teacher profile ID first
                val teachersRes = teachersRepository.getTeachers(schoolId).first()
                val teacherProfileId =
                    teachersRes.data?.find { teacher ->
                    teacher.userId == userId
                }?.id ?: run {
                    _state.update { it.copy(isLoading = false, errorMessage = "Profil enseignant introuvable / Teacher profile not found") }
                    return@launch
                }

                // Obtenir toutes les affectations de cet enseignant
                // Get all assignments for this teacher
                val assignmentsRes = assignmentRepository.getAssignmentsForSchool().first()
                if (assignmentsRes is Resource.Success) {
                    val myAssignments = assignmentsRes.data?.filter { it.teacherId == teacherProfileId } ?: emptyList()
                    val allEntries = mutableListOf<ScheduleEntryDto>()

                    // Récupérer l'horaire pour chaque affectation
                    // Fetch schedule for each assignment
                    for (assignment in myAssignments) {
                        val schedRes = schoolRepository.getScheduleEntriesByTeachingAssignmentIdAndWeekNumber(assignment.id, weekNumber).first()
                        if (schedRes is Resource.Success) {
                            schedRes.data?.let { allEntries.addAll(it) }
                        }
                    }

                    _state.update { it.copy(
                        isLoading = false,
                        scheduleEntries = allEntries.sortedWith(compareBy({ it.dayOfWeek }, { it.startDayHourTime }))
                    ) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Erreur lors du chargement des affectations / Error loading assignments") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Erreur inconnue / Unknown error") }
            }
        }
    }
}
