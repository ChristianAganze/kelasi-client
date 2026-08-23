package com.drcmind.kelasisuite.ui.teacheradmin.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.domain.model.teacher.AttendanceStatus
import com.drcmind.kelasisuite.domain.model.teacher.StudentEval
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class ClassesState(
    val availableClasses: List<TeachingAssignmentDTO> = emptyList(),
    val selectedClass: TeachingAssignmentDTO? = null,
    val evaluationPeriods: List<EvaluationPeriodDTO> = emptyList(),
    val selectedPeriod: EvaluationPeriodDTO? = null,
    val isLoadingClasses: Boolean = false,
    val errorMessage: String? = null,
    val isLoadingStudents: Boolean = false,
    val studentErrorMessage: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null,
    val evaluationType: EvaluationType = EvaluationType.ATTENDANCE,
    val students: List<StudentEval> = emptyList()
)

enum class EvaluationType {
    ATTENDANCE, GRADES
}

class ClassesViewModel(
    private val settingsStorage: SettingsStorage,
    private val teachersRepository: TeachersRepository,
    private val assignmentRepository: AssignmentRepository,
    private val studentsRepository: com.drcmind.kelasisuite.data.repository.students.StudentsRepository,
    private val evaluationRepository: com.drcmind.kelasisuite.data.repository.teacher.EvaluationRepository,
    private val schoolRepository: com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ClassesState())
    val state: StateFlow<ClassesState> = _state.asStateFlow()

    init {
        fetchMyClasses()
        fetchEvaluationPeriods()
    }

    fun retryClasses() {
        _state.update { it.copy(errorMessage = null) }
        fetchMyClasses()
    }

    fun retryStudents() {
        _state.update { it.copy(studentErrorMessage = null) }
        fetchStudentsForClass(_state.value.selectedClass?.classId)
    }

    private fun fetchEvaluationPeriods() {
        viewModelScope.launch {
            schoolRepository.getEvaluationPeriodsBySchool().collect { resource ->
                when (resource) {
                    is Resource.Error -> _state.update { it.copy(studentErrorMessage = resource.message) }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val periods = resource.data?.values?.flatten() ?: emptyList()
                        _state.update { it.copy(evaluationPeriods = periods) }
                    }
                }
            }
        }
    }

    fun selectPeriod(period: EvaluationPeriodDTO) {
        _state.update { it.copy(selectedPeriod = period) }
    }

    private fun fetchMyClasses() {
        val schoolId = settingsStorage.getSchool()?.id
        val userId = settingsStorage.getUserInfo().userId
        if (schoolId == null || userId == null) {
            _state.update {
                it.copy(isLoadingClasses = false, errorMessage = "Connexion incomplète : impossible de charger vos classes.")
            }
            return
        }

        viewModelScope.launch {
            teachersRepository.getTeachers(schoolId).collect { teachersResource ->
                when (teachersResource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoadingClasses = true, errorMessage = null) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoadingClasses = false, errorMessage = teachersResource.message) }
                    }
                    is Resource.Success -> {
                        val myProfile = teachersResource.data?.find { it.userId == userId }
                        if (myProfile != null) {
                            fetchAssignmentsForTeacher(myProfile.id)
                        } else {
                            _state.update { it.copy(isLoadingClasses = false, errorMessage = "Profil enseignant non trouvé.") }
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchAssignmentsForTeacher(teacherProfileId: Long) {
        assignmentRepository.getAssignmentsForSchool().collect { assignmentsResource ->
            when (assignmentsResource) {
                is Resource.Loading -> {
                     _state.update { it.copy(isLoadingClasses = true) }
                }
                is Resource.Error -> {
                     _state.update { it.copy(isLoadingClasses = false, errorMessage = assignmentsResource.message) }
                }
                is Resource.Success -> {
                     val myAssignments = assignmentsResource.data?.filter { it.teacherId == teacherProfileId } ?: emptyList()
                     val firstClass = myAssignments.firstOrNull()
                     _state.update { 
                         it.copy(
                             isLoadingClasses = false, 
                             availableClasses = myAssignments,
                             selectedClass = firstClass
                         ) 
                     }
                     if (firstClass != null) {
                         fetchStudentsForClass(firstClass.classId)
                     }
                }
            }
        }
    }

    private fun fetchStudentsForClass(classId: Long?) {
        if (classId == null) {
            _state.update { it.copy(isLoadingStudents = false) }
            return
        }
        viewModelScope.launch {
            studentsRepository.getStudentsForClass(classId).collect { studentsResource ->
                when (studentsResource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoadingStudents = true, studentErrorMessage = null) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoadingStudents = false, studentErrorMessage = studentsResource.message) }
                    }
                    is Resource.Success -> {
                        val mappedStudents = studentsResource.data?.map { dto ->
                            StudentEval(
                                id = dto.id.toString(),
                                firstName = dto.firstName,
                                lastName = dto.lastName
                            )
                        } ?: emptyList()
                        _state.update { it.copy(isLoadingStudents = false, students = mappedStudents) }
                    }
                }
            }
        }
    }

    fun selectClass(assignment: TeachingAssignmentDTO) {
        _state.update { it.copy(selectedClass = assignment) }
        fetchStudentsForClass(assignment.classId)
    }

    fun setEvaluationType(type: EvaluationType) {
        _state.update { it.copy(evaluationType = type) }
    }

    fun updateAttendance(studentId: String, status: AttendanceStatus) {
        _state.update { state ->
            val updatedStudents = state.students.map {
                if (it.id == studentId) it.copy(attendance = status) else it
            }
            state.copy(students = updatedStudents)
        }
    }

    fun updateGrade(studentId: String, grade: String) {
        _state.update { state ->
            val updatedStudents = state.students.map {
                if (it.id == studentId) it.copy(grade = grade) else it
            }
            state.copy(students = updatedStudents)
        }
    }

    fun dismissSnackbar() {
        _state.update { it.copy(saveSuccess = false, saveError = null) }
    }

    fun saveEvaluations() {
        val currentState = _state.value
        val assignment = currentState.selectedClass ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            // Get current date string 
            val currentDate = kotlinx.datetime.Clock.System.now().toString()

            var hasError = false
            for (student in currentState.students) {
                val dto = com.drcmind.kelasisuite.data.datasource.remote.dto.StudentEvaluationDTO(
                    studentId = student.id.toLong(),
                    teachingAssignmentId = assignment.id,
                    type = if (currentState.evaluationType == EvaluationType.ATTENDANCE) 
                               com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationTypeDTO.ATTENDANCE 
                           else com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationTypeDTO.GRADE,
                    value = if (currentState.evaluationType == EvaluationType.ATTENDANCE) student.attendance.name else student.grade,
                    date = currentDate,
                    evaluationPeriodId = currentState.selectedPeriod?.id
                )
                
                // Submit one by one for now (ideally bulk endpoint)
                evaluationRepository.submitStudentEvaluation(dto).collect { res ->
                    if (res is Resource.Error) hasError = true
                }
            }
            
            _state.update { 
                it.copy(
                    isSaving = false, 
                    saveSuccess = !hasError,
                    saveError = if (hasError) "Certaines évaluations n'ont pas pu être sauvegardées." else null
                ) 
            }
        }
    }
}