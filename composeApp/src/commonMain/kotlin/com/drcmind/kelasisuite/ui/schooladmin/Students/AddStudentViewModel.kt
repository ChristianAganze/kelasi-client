package com.drcmind.kelasisuite.ui.schooladmin.Students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.domain.dto.StudentCreationRequest
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Clock

class AddStudentViewModel(
    private val studentsRepository: StudentsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddStudentState())
    val state: StateFlow<AddStudentState> = _state.asStateFlow()

    fun onLastNameChange(value: String) {
        _state.value = _state.value.copy(lastName = value)
    }

    fun onFirstNameChange(value: String) {
        _state.value = _state.value.copy(firstName = value)
    }

    fun onDateOfBirthChange(value: String) {
        _state.value = _state.value.copy(dateOfBirth = value)
    }

    fun onReligionChange(value: String) {
        _state.value = _state.value.copy(religion = value)
    }

    fun onPreviousSchoolChange(value: String) {
        _state.value = _state.value.copy(previousSchool = value)
    }

    fun onAddressChange(value: String) {
        _state.value = _state.value.copy(address = value)
    }

    fun createStudent() {
        val currentState = _state.value

        // Basic validation
        if (currentState.lastName.isBlank() || currentState.firstName.isBlank() || currentState.dateOfBirth.isBlank() || currentState.address.isBlank()) {
            _state.value =
                currentState.copy(error = "Le nom, prénom, date de naissance et adresse sont obligatoires")
            return
        }

        val dob = try {
            if (currentState.dateOfBirth.isNotBlank()) LocalDate.parse(currentState.dateOfBirth) else null
        } catch (e: Exception) {
            _state.value = currentState.copy(error = "Format de date invalide (AAAA-MM-JJ)")
            return
        }

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
            schoolId = 1 // TODO: Get actual schoolId and verify the SER number down and change it
        )

        studentsRepository.createStudent(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, error = null)
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = resource.message)
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }
}

data class AddStudentState(
    val lastName: String = "",
    val firstName: String = "",
    val dateOfBirth: String = "",
    val religion: String = "",
    val previousSchool: String = "",
    val address: String = "",
    val studentIdNumber: String = "STU-${(100..999).random()}-${
        Clock.System.now().toEpochMilliseconds()
    }",
    val sernieNumber: String = "SER-${(100..999).random()}-${
        Clock.System.now().toEpochMilliseconds()
    }",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
