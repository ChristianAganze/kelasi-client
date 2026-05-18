package com.drcmind.kelasisuite.ui.schooladmin.staff_hr.teachers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.users.UsersRepository
import com.drcmind.kelasisuite.domain.dto.Address
import com.drcmind.kelasisuite.domain.dto.TeacherProfileDTO
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
import kotlin.time.Clock

class TeachersViewModel(
    private val teachersRepository: TeachersRepository,
    private val usersRepository: UsersRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    // --- Teachers List State ---
    private val _listState = MutableStateFlow(TeachersUiState())
    val listState: StateFlow<TeachersUiState> = _listState.asStateFlow()
    private var allTeachers: List<TeacherItem> = emptyList()

    // --- Teacher Detail State ---
    private val _detailState = MutableStateFlow(TeacherDetailsState())
    val detailState: StateFlow<TeacherDetailsState> = _detailState.asStateFlow()

    // --- Add/Edit Teacher State ---
    private val _formState = MutableStateFlow(AddTeacherState())
    val formState: StateFlow<AddTeacherState> = _formState.asStateFlow()
    private var allUsers: List<UserDTO> = emptyList()
    private var teacherUserIds: Set<Long> = emptySet()

    init {
        loadTeachers()
        loadUsersData()
    }

    // --- List Logic ---
    fun loadTeachers() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        teachersRepository.getTeachers(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _listState.update { it.copy(isLoading = true) }
                is Resource.Success -> {
                    allTeachers = resource.data?.map { it.toTeachersItem() } ?: emptyList()
                    filterTeachers()
                }
                is Resource.Error -> _listState.update { it.copy(isLoading = false) }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _listState.update { it.copy(searchQuery = query) }
        filterTeachers()
    }

    private fun filterTeachers() {
        val query = _listState.value.searchQuery.lowercase()
        val filtered = allTeachers.filter { teacher ->
            teacher.fullName.lowercase().contains(query) ||
                    (teacher.payrollId?.lowercase()?.contains(query) ?: false) ||
                    teacher.qualifications.lowercase().contains(query)
        }
        _listState.update {
            it.copy(
                isLoading = false,
                teachers = filtered,
                totalTeachers = allTeachers.size
            )
        }
    }

    private fun TeacherProfileDTO.toTeachersItem() = TeacherItem(
        id = id.toString(),
        userId = userId.toString(),
        fullName = fullName,
        address = address,
        payrollId = payrollId,
        qualifications = qualifications
    )

    // --- Detail Logic ---
    fun loadTeacherDetail(teacherId: Long) {
        teachersRepository.getTeacher(teacherId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _detailState.update { it.copy(isLoading = true, error = null) }
                is Resource.Success -> _detailState.update { it.copy(isLoading = false, teacher = resource.data) }
                is Resource.Error -> _detailState.update { it.copy(isLoading = false, error = resource.message) }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // --- Form Logic (Add/Edit) ---
    private fun loadUsersData() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        _formState.update { it.copy(isLoading = true) }

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
                    _formState.update { it.copy(isLoading = false) }
                }
                is Resource.Error -> _formState.update { it.copy(isLoading = false, error = resource.message) }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun filterAndDisplayUsers() {
        val query = _formState.value.searchQuery.lowercase()
        val filtered = allUsers.filter { user ->
            !teacherUserIds.contains(user.id) &&
                    (user.firstName.lowercase().contains(query) ||
                            user.lastName.lowercase().contains(query) ||
                            (user.email?.lowercase()?.contains(query) ?: false) ||
                            (user.phone?.contains(query) ?: false))
        }
        _formState.update { it.copy(users = filtered) }
    }

    fun onUserSearchQueryChange(query: String) {
        _formState.update { it.copy(searchQuery = query) }
        filterAndDisplayUsers()
    }

    fun onUserSelected(user: UserDTO) {
        _formState.update {
            it.copy(
                selectedUser = user,
                userId = user.id,
                fullName = "${user.firstName} ${user.lastName}",
                showUserList = false
            )
        }
    }

    fun onBackToUserList() {
        _formState.update { it.copy(showUserList = true, selectedUser = null, userId = null) }
    }

    fun onFullNameChange(value: String) = _formState.update { it.copy(fullName = value) }
    fun onQualificationsChange(value: String) = _formState.update { it.copy(qualifications = value) }
    fun onMaxHoursChange(value: String) = _formState.update { it.copy(maxWeeklyHours = value) }
    fun onAddressChange(value: String) = _formState.update { it.copy(streetAddress = value) }
    fun onCityChange(value: String) = _formState.update { it.copy(city = value) }
    fun onHireDateChange(value: String) = _formState.update { it.copy(hireDate = value) }
    fun onProvinceChange(value: String) = _formState.update { it.copy(province = value) }

    fun prepareFormForEdit(teacherId: Long) {
        teachersRepository.getTeacher(teacherId).onEach { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { teacher ->
                    _formState.update {
                        it.copy(
                            selectedUser = allUsers.firstOrNull { user -> user.id == teacher.userId },
                            userId = teacher.userId,
                            fullName = teacher.fullName,
                            qualifications = teacher.qualifications,
                            payrollId = teacher.payrollId ?: "",
                            city = teacher.address.cityTerritory,
                            province = teacher.address.province,
                            streetAddress = teacher.address.streetAndNumber ?: "",
                            showUserList = false,
                            isSuccess = false,
                            error = null
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun resetForm() {
        _formState.value = AddTeacherState()
        loadUsersData()
    }

    fun saveTeacher(teacherId: Long? = null) {
        val currentState = _formState.value
        val userId = currentState.userId

        if (teacherId == null && userId == null) {
            _formState.update { it.copy(error = "Veuillez sélectionner un utilisateur") }
            return
        }

        if (currentState.fullName.isBlank() || currentState.qualifications.isBlank() || currentState.hireDate.isBlank()) {
            _formState.update { it.copy(error = "Veuillez remplir tous les champs obligatoires") }
            return
        }

        val validatedHireDate = try {
            LocalDate.parse(currentState.hireDate)
        } catch (e: Exception) {
            _formState.update { it.copy(error = "Format de date invalide (AAAA-MM-JJ)") }
            return
        }

        val request = TeacherProfileRequest(
            userId = userId ?: 0,
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

        val flow = if (teacherId == null) {
            teachersRepository.createTeacher(request)
        } else {
            teachersRepository.updateTeacher(teacherId, request)
        }

        flow.onEach { resource ->
            when (resource) {
                is Resource.Loading -> _formState.update { it.copy(isLoading = true, error = null) }
                is Resource.Success -> _formState.update { it.copy(isLoading = false, isSuccess = true) }
                is Resource.Error -> _formState.update { it.copy(isLoading = false, error = resource.message) }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }
}

// --- Data Classes ---

data class TeacherItem(
    val id: String,
    val userId: String,
    val fullName: String,
    val address: Address,
    val payrollId: String?,
    val qualifications: String,
)

data class TeachersUiState(
    val teachers: List<TeacherItem> = emptyList(),
    val totalTeachers: Int = 0,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

data class TeacherDetailsState(
    val teacher: TeacherProfileDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

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
    val isSuccess: Boolean = false,
    val userId: Long? = null
)
