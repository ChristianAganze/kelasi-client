package com.drcmind.kelasisuite.ui.schooladmin.teachers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.domain.dto.Address
import com.drcmind.kelasisuite.domain.dto.TeacherProfileRequest
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddTeacherViewModel(
    private val teachersRepository: TeachersRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddTeacherState())
    val state: StateFlow<AddTeacherState> = _state.asStateFlow()

    fun onFullNameChange(value: String) = _state.update { it.copy(fullName = value) }
    fun onQualificationsChange(value: String) = _state.update { it.copy(qualifications = value) }
    fun onMaxHoursChange(value: String) = _state.update { it.copy(maxWeeklyHours = value) }
    fun onAddressChange(value: String) = _state.update { it.copy(streetAddress = value) }
    fun onCityChange(value: String) = _state.update { it.copy(city = value) }
    fun onHireDateChange(value: String) = _state.update { it.copy(hireDate = value) }
    fun onProvinceChange(value: String) = _state.update { it.copy( province = value) }

    fun createTeacher() {
        val currentState = _state.value

        // 1. Validation des champs
        if (currentState.fullName.isBlank() || currentState.qualifications.isBlank() || currentState.hireDate.isBlank()) {
            _state.update { it.copy(error = "Veuillez remplir tous les champs obligatoires") }
            return
        }

        // 2. Validation de la date
        val validatedHireDate = try {
            LocalDate.parse(currentState.hireDate)
        } catch (e: Exception) {
            _state.update { it.copy(error = "Format de date invalide (AAAA-MM-JJ)") }
            return
        }

        // 3. Préparation de la requête
        val request = TeacherProfileRequest(
            userId = 2, // Id temporaire ou généré
            payrollId = currentState.payrollId,
            qualifications = currentState.qualifications,
            hireDate = validatedHireDate,
            maxWeeklyHours = currentState.maxWeeklyHours.toIntOrNull() ?: 40,
            resumeUrl = null,
            address = Address(
                province = currentState.province,
                cityTerritory = currentState.city,
                streetAndNumber = currentState.streetAddress
            )
        )

        // 4. Appel au repository
        teachersRepository.createTeacher(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = resource.message) }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }
}

data class AddTeacherState(
    val fullName: String = "",
    val qualifications: String = "",
    val maxWeeklyHours: String = "",
    val streetAddress: String = "",
    val city: String = "",
    val province: String = "",
    val hireDate: String = "",
    val payrollId: String = "TestID",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)