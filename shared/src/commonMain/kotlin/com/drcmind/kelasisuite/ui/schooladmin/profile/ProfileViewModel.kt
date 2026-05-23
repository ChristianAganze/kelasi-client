package com.drcmind.kelasisuite.ui.schooladmin.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.profile.ProfileRepository
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

data class ProfileState(
    val user: UserDTO? = null,
    val school: SchoolDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        val userInfo = settingsStorage.getUserInfo()
        val userId = userInfo.userId
        val schoolId = userInfo.schoolId

        if (userId != null && schoolId != null) {
            loadProfile(userId = userId, schoolId = schoolId)
        } else {
            _state.value = _state.value.copy(error = "Informations de session manquantes ")
        }
    }

    fun loadProfile(userId: Long, schoolId: Long) {
        _state.value = _state.value.copy(isLoading = true)

        combine(
            profileRepository.getUser(userId),
            profileRepository.getSchool(schoolId)
        ) { userRes, schoolRes ->
            when {
                userRes is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = userRes.message)
                schoolRes is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = schoolRes.message)
                (userRes is Resource.Success) && (schoolRes is Resource.Success) -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = userRes.data,
                        school = schoolRes.data,
                        error = null
                    )
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }
}
