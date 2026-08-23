package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.program_radar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.remote.dto.ProgramRadarClassDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ProgramRadarDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ProgramRadarSubjectDto
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

enum class RadarStatus {
    AHEAD,
    ON_TRACK,
    DELAYED
}

data class SubjectCoverageUi(
    val subjectName: String,
    val nationalTarget: Float,
    val realized: Float
)

data class ClassRadarUi(
    val classId: Long,
    val className: String,
    val sectionLabel: String,
    val studentsCount: Int,
    val nationalTarget: Float,
    val realized: Float,
    val status: RadarStatus,
    val subjects: List<SubjectCoverageUi>
)

data class ProgramRadarUiState(
    val classes: List<ClassRadarUi> = emptyList(),
    val selectedClass: ClassRadarUi? = null,
    val weekNumber: Int? = null,
    val academicYearLabel: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProgramRadarViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgramRadarUiState())
    val uiState: StateFlow<ProgramRadarUiState> = _uiState.asStateFlow()

    init {
        loadRadar()
    }

    fun loadRadar() {
        schoolRepository.getProgramRadar().onEach { resource ->
            _uiState.update { currentState ->
                when (resource) {
                    is Resource.Loading -> currentState.copy(isLoading = true, error = null)

                    is Resource.Success -> {
                        val data = resource.data
                        val classes = data?.classes?.map { it.toUi() } ?: emptyList()
                        val selectedClass = currentState.selectedClass
                            ?.let { selected -> classes.firstOrNull { it.classId == selected.classId } }
                            ?: classes.firstOrNull()
                        currentState.copy(
                            classes = classes,
                            selectedClass = selectedClass,
                            weekNumber = data?.weekNumber,
                            academicYearLabel = data?.academicYearLabel,
                            isLoading = false,
                            error = null
                        )
                    }

                    is Resource.Error -> currentState.copy(isLoading = false, error = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun selectClass(classId: Long) {
        _uiState.update { state ->
            state.copy(selectedClass = state.classes.firstOrNull { it.classId == classId })
        }
    }
}

private fun ProgramRadarDto.toUi(): List<ClassRadarUi> = classes.map { it.toUi() }

private fun ProgramRadarClassDto.toUi(): ClassRadarUi {
    val subjectProgress = subjects.map { it.toUi() }
    val target = subjectProgress.map { it.nationalTarget }.averageOrZero()
    val realized = subjectProgress.map { it.realized }.averageOrZero()
    val status = when {
        realized < target - 0.05f -> RadarStatus.DELAYED
        realized > target + 0.05f -> RadarStatus.AHEAD
        else -> RadarStatus.ON_TRACK
    }
    return ClassRadarUi(
        classId = classId,
        className = className,
        sectionLabel = sectionLabel,
        studentsCount = studentsCount,
        nationalTarget = target,
        realized = realized,
        status = status,
        subjects = subjectProgress
    )
}

private fun ProgramRadarSubjectDto.toUi(): SubjectCoverageUi = SubjectCoverageUi(
    subjectName = subjectName,
    nationalTarget = nationalTarget.coercedProgress(),
    realized = realized.coercedProgress()
)

private fun Float.coercedProgress(): Float = if (isNaN()) 0f else coerceIn(0f, 1f)

private fun List<Float>.averageOrZero(): Float = if (isEmpty()) 0f else average().toFloat().coercedProgress()
