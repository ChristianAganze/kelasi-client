package com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentCreationRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.UpdateEnrollmentRequest
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.datetime.LocalDate
import kotlin.time.Clock

class StudentsViewModel(
    private val studentsRepository: StudentsRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _listState = MutableStateFlow(StudentUiState())
    val listState: StateFlow<StudentUiState> = _listState.asStateFlow()
    private val _state = MutableStateFlow(StudentUiState())
    val state: StateFlow<StudentUiState> = _state.asStateFlow()

    // --- Student Detail State ---
    private val _detailState = MutableStateFlow(StudentDetailState())
    val detailState: StateFlow<StudentDetailState> = _detailState.asStateFlow()

    // --- Add/Edit Student State ---
    private val _formState = MutableStateFlow(AddStudentState())
    val formState: StateFlow<AddStudentState> = _formState.asStateFlow()

    private var allStudents: List<StudentDTO> = emptyList()

    init {
        loadStudents()
    }

    fun loadStudents() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        studentsRepository.getStudents(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _listState.update { it.copy(isLoading = true) }

                is Resource.Success -> {
                    allStudents = resource.data ?: emptyList()
                    filterStudents()
                }

                is Resource.Error -> _listState.update { it.copy(isLoading = false) }
            }
        }.launchIn(viewModelScope)
    }

    fun setActiveStudent(student: StudentDTO?) {
        _listState.update {
            it.copy(
                activeStudent = student,
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _listState.update { it.copy(searchQuery = query) }
        filterStudents()
    }

    private fun filterStudents() {
        val query = _listState.value.searchQuery.lowercase()
        val filtered = allStudents.filter { student ->
            student.fullName.lowercase().contains(query)
        }
        _listState.update {
            it.copy(
                isLoading = false,
                students = filtered,
            )
        }
    }

    // --- Detail Logic ---
    fun loadStudentDetail(studentId: Long) {
        studentsRepository.getStudent(studentId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _detailState.update { it.copy(isLoading = true, error = null) }
                is Resource.Success -> _detailState.update { it.copy(isLoading = false, student = resource.data) }
                is Resource.Error -> _detailState.update { it.copy(isLoading = false, error = resource.message) }
            }
        }.launchIn(viewModelScope)
    }

    fun enrollStudent(studentId: Long, classId: Long, academicYearId: Long) {
        val request = EnrollmentRequest(studentId, classId, academicYearId)
        studentsRepository.enrollStudent(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _detailState.update {
                    it.copy(
                        isLoadingEnrollment = true,
                        enrollmentError = null
                    )
                }

                is Resource.Success -> {
                    _detailState.update { it.copy(isLoadingEnrollment = false, student = resource.data) }
                    loadStudents() // Refresh list
                }

                is Resource.Error -> _detailState.update {
                    it.copy(
                        isLoadingEnrollment = false,
                        enrollmentError = resource.message
                    )
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun updateEnrollment(enrollmentId: Long, newClassId: Long) {
        val request = UpdateEnrollmentRequest(newClassId)
        studentsRepository.updateEnrollment(enrollmentId, request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _detailState.update {
                    it.copy(
                        isLoadingEnrollment = true,
                        enrollmentError = null
                    )
                }

                is Resource.Success -> {
                    _detailState.update { it.copy(isLoadingEnrollment = false, student = resource.data) }
                    loadStudents() // Refresh list
                }

                is Resource.Error -> _detailState.update {
                    it.copy(
                        isLoadingEnrollment = false,
                        enrollmentError = resource.message
                    )
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // --- Form Logic (Add/Edit) ---
    fun onLastNameChange(value: String) = _formState.update { it.copy(lastName = value) }
    fun onFirstNameChange(value: String) = _formState.update { it.copy(firstName = value) }
    fun onDateOfBirthChange(value: String) = _formState.update { it.copy(dateOfBirth = value) }
    fun onReligionChange(value: String) = _formState.update { it.copy(religion = value) }
    fun onPreviousSchoolChange(value: String) = _formState.update { it.copy(previousSchool = value) }
    fun onAddressChange(value: String) = _formState.update { it.copy(address = value) }

    fun prepareFormForEdit(studentId: Long) {
        studentsRepository.getStudent(studentId).onEach { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { student ->
                    _formState.update {
                        it.copy(
                            lastName = student.lastName,
                            firstName = student.firstName,
                            dateOfBirth = student.dateOfBirth?.toString() ?: "",
                            religion = student.religion ?: "",
                            previousSchool = student.previousSchool,
                            address = student.address ?: "",
                            studentIdNumber = student.studentIdNumber,
                            sernieNumber = student.sernieNumber ?: "",
                            isSuccess = false,
                            error = null
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun resetForm() {
        _formState.value = AddStudentState()
    }

    fun saveStudent(studentId: Long? = null) {
        val currentState = _formState.value

        if (currentState.lastName.isBlank() || currentState.firstName.isBlank() || currentState.dateOfBirth.isBlank() || currentState.address.isBlank()) {
            _formState.update { it.copy(error = "Le nom, prénom, date de naissance et adresse sont obligatoires") }
            return
        }

        val dob = try {
            if (currentState.dateOfBirth.isNotBlank()) LocalDate.parse(currentState.dateOfBirth) else null
        } catch (e: Exception) {
            _formState.update { it.copy(error = "Format de date invalide (AAAA-MM-JJ)") }
            return
        }

        val schoolId = settingsStorage.getUserInfo().schoolId ?: 1

        val request = StudentCreationRequest(
            studentIdNumber = currentState.studentIdNumber,
            sernieNumber = currentState.sernieNumber.ifBlank { null },
            lastName = currentState.lastName,
            firstName = currentState.firstName,
            address = currentState.address.ifBlank { null },
            previousSchool = currentState.previousSchool,
            religion = currentState.religion.ifBlank { null },
            photoUrl = null,
            dateOfBirth = dob,
            schoolId = schoolId
        )

        val flow = if (studentId == null) {
            studentsRepository.createStudent(request)
        } else {
            studentsRepository.updateStudent(studentId, request)
        }

        flow.onEach { resource ->
            when (resource) {
                is Resource.Loading -> _formState.update { it.copy(isLoading = true, error = null) }
                is Resource.Success -> _formState.update { it.copy(isLoading = false, isSuccess = true) }
                is Resource.Error -> _formState.update { it.copy(isLoading = false, error = resource.message) }
            }
        }.launchIn(viewModelScope)
    }

}

enum class StudentStatus { ACTIVE, PROBATION, INACTIVE }

data class StudentUiState(
    val students: List<StudentDTO> = emptyList(),
    val activeStudent: StudentDTO? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error : String? = null,
)

data class StudentDetailState(
    val student: StudentDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoadingEnrollment: Boolean = false,
    val enrollmentError: String? = null
)

data class AddStudentState(
    val lastName: String = "",
    val firstName: String = "",
    val dateOfBirth: String = "",
    val religion: String = "",
    val previousSchool: String = "",
    val address: String = "",
    val studentIdNumber: String = "STU-${(100..999).random()}-${Clock.System.now().toEpochMilliseconds()}",
    val sernieNumber: String = "SER-${(100..999).random()}-${Clock.System.now().toEpochMilliseconds()}",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
