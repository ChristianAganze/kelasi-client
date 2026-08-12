package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teaching_assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.domain.util.Resource
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teachers.ActivityType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class TeachingAssignmentViewModel(
    private val teachingAssignmentRepository: AssignmentRepository
) : ViewModel(){
    val uiState : StateFlow<TeachingAssignmenState>
        field = MutableStateFlow(TeachingAssignmenState())


    init {
        loadSchoolTeachingAssignments()
    }



    fun onSearchQueryChange(query: String) {
        uiState.update { it.copy(searchQuery = query) }
        filterTeachers()
    }

    private fun filterTeachers() {
        val query = uiState.value.searchQuery.lowercase()
        val filtered = uiState.value.allTeachingAssignments.filter { assignment ->
            assignment.teacherName.lowercase().contains(query) ||
                    assignment.subjectName.lowercase().contains(query) ||
                    assignment.className.lowercase().contains(query)
        }
        uiState.update {
            it.copy(
                isLoading = false,
                teachingAssignments = filtered,
            )
        }
    }

    fun setActiveTeachingAssignment(assignment: TeachingAssignmentDTO) {
        uiState.update { it.copy(activeTeachinggAssignment = assignment) }
    }

    fun loadSchoolTeachingAssignments() {
        uiState.update { it.copy(isLoading = true, error = null) }
        teachingAssignmentRepository.getAssignmentsForSchool().onEach { resource ->
            when (resource) {
                is Resource.Loading -> uiState.update { it.copy(isLoading = true, error = null) }
                is Resource.Success -> {
                    val data = resource.data ?: listOf()
                    uiState.update {
                        it.copy(
                            isLoading = false,
                            teachingAssignments = data,
                            allTeachingAssignments = data
                        )
                    }
                }
                is Resource.Error -> {
                    uiState.update { it.copy(isLoading = false, teachingAssignments = listOf(), error = resource.message) }
                }
            }
        }.launchIn(viewModelScope)
    }

}


data class TeachingAssignmentActivityUI(
    val title: String,
    val description: String,
    val timestamp: String,
    val type: ActivityType
)


data class TeachingAssignmenState(
    val teachingAssignments : List<TeachingAssignmentDTO> = emptyList(),
    val allTeachingAssignments : List<TeachingAssignmentDTO> = emptyList(),
    val activeTeachinggAssignment : TeachingAssignmentDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery : String = "",
    val activites : List<TeachingAssignmentActivityUI> = listOf(
        TeachingAssignmentActivityUI(
            title = "Préparation de leçon",
            description = "Semaine 12 pour verifier la classification des enjeux...",
            timestamp = "Ajourd'hui à 13h",
            type = ActivityType.LESSON_PREPARATION
        ),
        TeachingAssignmentActivityUI(
            title = "Journal de classe",
            description = "Semaine 12 pour verifier la classification des enjeux...",
            timestamp = "Ajourd'hui à 9h",
            type = ActivityType.CLASS_LOG
        )
    ),
)