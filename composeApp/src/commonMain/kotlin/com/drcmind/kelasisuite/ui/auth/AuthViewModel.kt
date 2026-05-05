package com.drcmind.kelasisuite.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.AuthRepository
import com.drcmind.kelasisuite.domain.dto.LoginRequest
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val username: String, val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class AuthViewModelState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val authState: AuthState = AuthState.Idle,
    val emailError: String? = null,
    val passwordError: String? = null
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthViewModelState())
    val state: StateFlow<AuthViewModelState> = _state.asStateFlow()

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email, emailError = null)
    }

    fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password, passwordError = null)
    }

    fun updateRememberMe(rememberMe: Boolean) {
        _state.value = _state.value.copy(rememberMe = rememberMe)
    }

    fun login() {
        if (validateInputs()) {
            _state.value = _state.value.copy(authState = AuthState.Loading)
            viewModelScope.launch {
                authRepository.login(
                    LoginRequest(
                        username = _state.value.email,
                        password = _state.value.password
                    )
                ).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _state.value = _state.value.copy(
                                authState = AuthState.Success(
                                    username = resource.data?.username ?: "",
                                    role = resource.data?.roles?.firstOrNull() ?: ""
                                ),
                                email = "",
                                password = ""
                            )
                        }

                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                authState = AuthState.Error(
                                    message = resource.message ?: "Une erreur est survenue"
                                )
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = _state.value.copy(authState = AuthState.Loading)
                        }

                        else -> {
                            // Handle Idle state
                        }
                    }
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        val currentState = _state.value

        if (currentState.email.isEmpty()) {
            _state.value = currentState.copy(emailError = "L'adresse e-mail est requise")
            isValid = false
        } else if (!currentState.email.contains("@")) {
            _state.value = currentState.copy(emailError = "Veuillez entrer une adresse e-mail valide")
            isValid = false
        }

        if (currentState.password.isEmpty()) {
            _state.value = currentState.copy(passwordError = "Le mot de passe est requis")
            isValid = false
        }
//        else if (currentState.password.length < 6) {
//            _state.value = currentState.copy(passwordError = "Le mot de passe doit contenir au moins 6 caractères")
//            isValid = false
//        }

        return isValid
    }

    fun clearError() {
        _state.value = _state.value.copy(authState = AuthState.Idle)
    }
}