package com.drcmind.kelasisuite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.auth.AuthRepository
import com.drcmind.kelasisuite.domain.model.UserRole
import com.drcmind.kelasisuite.navigation.Route
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class AppViewModel(
    private val settingsStorage: SettingsStorage,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _navigationRouteState = MutableStateFlow<Route>(Route.Loading)
    val navigationState = _navigationRouteState.asStateFlow()

    // StateFlow to expose the remaining token validity time (as Duration)
    private val _tokenRemainingDuration = MutableStateFlow(Duration.ZERO)
    val tokenRemainingDuration = _tokenRemainingDuration.asStateFlow()

    private var expirationCheckJob: Job? = null

    // How often to check token expiration (e.g., every 30 seconds)
    private val TOKEN_CHECK_INTERVAL: Duration = 30.seconds

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            val userInfo = settingsStorage.getUserInfo()
            val isLoggedIn = !userInfo.token.isNullOrEmpty()

            if (isLoggedIn) {
                if (settingsStorage.isTokenExpired()) {
                    handleTokenExpired()
                } else {
                    if (userInfo.userId == null || userInfo.schoolId == null) {
                        authRepository.fetchAndSaveCurrentUser().collect {  }
                    }
                    when (userInfo.role) {
                        UserRole.ROLE_SUPER_USER.name -> _navigationRouteState.value = Route.SystemAdmin
                        UserRole.ROLE_SCHOOL_ADMIN.name -> _navigationRouteState.value = Route.SchoolAdmin
                        UserRole.ROLE_TEACHER.name -> _navigationRouteState.value = Route.TeacherAdmin
                        UserRole.ROLE_PARENT.name -> _navigationRouteState.value = Route.ParentAdmin
                    }
                    startTokenExpirationCheckTimer() // Start timer if token is valid
                }
            } else {
                _navigationRouteState.value = Route.Auth
                stopTokenExpirationCheckTimer() // Stop timer if not logged in
            }
            println(userInfo)
        }
    }

    private fun startTokenExpirationCheckTimer() {
        expirationCheckJob?.cancel() // Cancel any previous job
        expirationCheckJob = viewModelScope.launch {
            while (true) {
                val expirationInstant = settingsStorage.getTokenExpirationDate()
                if (expirationInstant == null) {
                    // Token isn't found or malformed (e.g., `exp` missing), treat as expired
                    println("Token expiration date not found or malformed. Handling as expired.")
                    handleTokenExpired()
                    break // Exit the timer loop
                }

                val remainingDuration = expirationInstant - kotlinx.datetime.Clock.System.now()
                _tokenRemainingDuration.value = remainingDuration

                if (remainingDuration <= Duration.ZERO) {
                    println("Token has expired. Handling logout.")
                    handleTokenExpired()
                    break // Token expired, exit the timer loop
                }

                delay(TOKEN_CHECK_INTERVAL)
            }
        }
    }

    private fun stopTokenExpirationCheckTimer() {
        expirationCheckJob?.cancel()
        expirationCheckJob = null
        _tokenRemainingDuration.value = Duration.ZERO // Reset remaining time
        println("Token expiration check timer stopped.")
    }

    private fun handleTokenExpired() {
        viewModelScope.launch {
            settingsStorage.clearUserInfo()
            _navigationRouteState.value = Route.Auth
            stopTokenExpirationCheckTimer()
            println("User data cleared and navigated to login due to token expiration.")
        }
    }

    fun logout() {
        println("AppViewModel: Logout initiated by UI.")
        handleTokenExpired() // Calls the internal function that handles cleanup and navigation
    }

    fun setNavigationState(route: Route) {
        _navigationRouteState.value = route
    }

    // Don't forget to cancel jobs when the ViewModel is no longer needed
    override fun onCleared() {
        super.onCleared()
        expirationCheckJob?.cancel()
    }
}