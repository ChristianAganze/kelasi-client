package com.drcmind.kelasisuite.ui.teacheradmin.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.data.repository.profile.ProfileRepository
import com.drcmind.kelasisuite.domain.model.UserInfo
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class TeacherSettingsState(
    val userInfo: UserInfo? = null,
    val userProfile: UserDTO? = null,
    val school: SchoolDTO? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

class TeacherSettingsViewModel(
    private val settingsStorage: SettingsStorage,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TeacherSettingsState(
            userInfo = settingsStorage.getUserInfo(),
            school = settingsStorage.getSchool()
        )
    )
    val uiState: StateFlow<TeacherSettingsState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val currentInfo = settingsStorage.getUserInfo()
        _uiState.update { it.copy(userInfo = currentInfo, isLoading = true, error = null) }

        val userId = currentInfo.userId
        if (userId != null && userId > 0) {
            profileRepository.getUser(userId).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val userDto = result.data
                        _uiState.update {
                            it.copy(
                                userProfile = userDto,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }

        val schoolId = currentInfo.schoolId
        if (schoolId != null && schoolId > 0 && _uiState.value.school == null) {
            profileRepository.getSchool(schoolId).onEach { result ->
                if (result is Resource.Success && result.data != null) {
                    _uiState.update { it.copy(school = result.data) }
                    settingsStorage.saveSchool(result.data)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun saveLocalSettings(
        fullName: String,
        phone: String,
        qualification: String
    ) {
        val current = _uiState.value.userInfo ?: return
        val names = fullName.trim().split(" ", limit = 2)
        val fName = names.getOrNull(0) ?: current.firstName
        val lName = names.getOrNull(1) ?: current.lastName

        settingsStorage.saveUserInfo(
            token = current.token ?: "",
            username = current.username ?: "",
            role = current.role ?: "TEACHER",
            userId = current.userId,
            schoolId = current.schoolId,
            firstName = fName,
            lastName = lName
        )

        _uiState.update {
            it.copy(
                userInfo = settingsStorage.getUserInfo(),
                saveSuccess = true
            )
        }
    }

    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}
