package com.drcmind.kelasisuite.ui.schooladmin.teachers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.users.UsersRepository
import com.drcmind.kelasisuite.domain.dto.Address
import com.drcmind.kelasisuite.domain.dto.TeacherProfileRequest
import com.drcmind.kelasisuite.domain.dto.UserDTO
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
import kotlin.time.Clock

class AddTeacherViewModel(
    private val teachersRepository: TeachersRepository,
    private val usersRepository: UsersRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(AddTeacherState())
    val state: StateFlow<AddTeacherState> = _state.asStateFlow()

    private var allUsers: List<UserDTO> = emptyList()
    private var teacherUserIds: Set<Long> = emptySet()

    init {
        loadData()
    }

    private fun loadData() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return

        _state.update { it.copy(isLoading = true) }

        teachersRepository.getTeachers(schoolId).onEach { resource ->
            if (resource is Resource.Success) {
                teacherUserIds = resource.data?.map { it.userId }?.toSet() ?: emptySet()
                filterAndDisplayUsers()
            }
        }.launchIn(viewModelScope)

        usersRepository.getUserBySchoolId(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    allUsers = resource.data ?: emptyList()
                    filterAndDisplayUsers()
                    _state.update { it.copy(isLoading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = resource.message) }
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun filterAndDisplayUsers() {
        val query = _state.value.searchQuery.lowercase()
        val filtered = allUsers.filter { user ->
            !teacherUserIds.contains(user.id) &&
                    (user.firstName.lowercase().contains(query) ||
                            user.lastName.lowercase().contains(query) ||
                            (user.email?.lowercase()?.contains(query) ?: false) ||
                            (user.phone?.contains(query) ?: false))
        }
        _state.update { it.copy(users = filtered) }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        filterAndDisplayUsers()
    }

    fun onUserSelected(user: UserDTO) {
        _state.update {
            it.copy(
                selectedUser = user,
                fullName = "${user.firstName} ${user.lastName}",
                showUserList = false
            )
        }
    }

    fun onBackToUserList() {
        _state.update { it.copy(showUserList = true, selectedUser = null) }
    }

    fun onFullNameChange(value: String) = _state.update { it.copy(fullName = value) }
    fun onQualificationsChange(value: String) = _state.update { it.copy(qualifications = value) }
    fun onMaxHoursChange(value: String) = _state.update { it.copy(maxWeeklyHours = value) }
    fun onAddressChange(value: String) = _state.update { it.copy(streetAddress = value) }
    fun onCityChange(value: String) = _state.update { it.copy(city = value) }
    fun onHireDateChange(value: String) = _state.update { it.copy(hireDate = value) }
    fun onProvinceChange(value: String) = _state.update { it.copy(province = value) }

    fun createTeacher() {
        val currentState = _state.value
        val userId = currentState.selectedUser?.id ?: return

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
            userId = userId,
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
    val users: List<UserDTO> = emptyList(),
    val selectedUser: UserDTO? = null,
    val showUserList: Boolean = true,
    val fullName: String = "",
    val qualifications: String = "",
    val maxWeeklyHours: String = "",
    val streetAddress: String = "",
    val city: String = "",
    val province: String = "",
    val hireDate: String = "",
    val payrollId: String = "TCH-${(100..999).random()}-${Clock.System.now().toEpochMilliseconds()}",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
