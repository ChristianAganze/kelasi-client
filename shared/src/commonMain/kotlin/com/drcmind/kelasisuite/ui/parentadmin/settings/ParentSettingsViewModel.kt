package com.drcmind.kelasisuite.ui.parentadmin.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParentProfile(
    val id: Long = 1L,
    val firstName: String = "Jean-Paul",
    val lastName: String = "Mukwege",
    val phone: String = "+243 812 345 678",
    val secondaryPhone: String = "+243 998 765 432",
    val email: String = "jp.mukwege@gmail.com",
    val address: String = "Avenue de la Paix, n° 45, Gombe, Kinshasa",
    val profession: String = "Ingénieur Télécoms",
    val relationship: String = "Père & Tuteur Légal"
)

data class AssociatedChild(
    val id: Long,
    val matricule: String,
    val fullName: String,
    val className: String,
    val schoolName: String,
    val status: String = "Actif"
)

data class ParentSettingsState(
    val profile: ParentProfile = ParentProfile(),
    val isEditingProfile: Boolean = false,
    val isLinkingChildDialog: Boolean = false,
    val isChangePasswordDialog: Boolean = false,
    val isChangePinDialog: Boolean = false,

    // Notification Toggles
    val notifyAttendanceImmediate: Boolean = true,
    val notifyHomeworkAlerts: Boolean = true,
    val notifyGradeReports: Boolean = true,
    val notifyFinanceDueDates: Boolean = true,
    val channelPush: Boolean = true,
    val channelSms: Boolean = true,
    val channelEmail: Boolean = false,

    // Security & Auth
    val isBiometricEnabled: Boolean = true,
    val isQuickPinEnabled: Boolean = true,
    val quickPinCode: String = "1234",

    // General Preferences
    val selectedLanguage: String = "Français",
    val isDarkMode: Boolean = false,
    val isDataSaver: Boolean = false,

    // Associated Children
    val associatedChildren: List<AssociatedChild> = listOf(
        AssociatedChild(101L, "MAT-2024-4091", "Kavira Mukwege", "4ème Scientifique A", "Complexe Scolaire Kelasi Suite"),
        AssociatedChild(102L, "MAT-2025-5120", "Ephraim Mukwege", "2ème Secondaire B", "Complexe Scolaire Kelasi Suite")
    ),

    val successMessage: String? = null,
    val errorMessage: String? = null
)

class ParentSettingsViewModel(
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ParentSettingsState())
    val state: StateFlow<ParentSettingsState> = _state.asStateFlow()

    init {
        val userInfo = settingsStorage.getUserInfo()
        val fName = userInfo.firstName ?: _state.value.profile.firstName
        val lName = userInfo.lastName ?: _state.value.profile.lastName
        val mail = userInfo.username?.let { if (it.contains("@")) it else null } ?: _state.value.profile.email

        _state.update {
            it.copy(
                profile = it.profile.copy(
                    firstName = fName,
                    lastName = lName,
                    email = mail
                )
            )
        }
    }

    fun openEditProfile() {
        _state.update { it.copy(isEditingProfile = true) }
    }

    fun closeEditProfile() {
        _state.update { it.copy(isEditingProfile = false) }
    }

    fun saveProfile(updated: ParentProfile) {
        _state.update {
            it.copy(
                profile = updated,
                isEditingProfile = false,
                successMessage = "Profil et coordonnées mis à jour avec succès !"
            )
        }
    }

    fun toggleAttendanceNotification(enabled: Boolean) {
        _state.update { it.copy(notifyAttendanceImmediate = enabled) }
    }

    fun toggleHomeworkNotification(enabled: Boolean) {
        _state.update { it.copy(notifyHomeworkAlerts = enabled) }
    }

    fun toggleGradeReports(enabled: Boolean) {
        _state.update { it.copy(notifyGradeReports = enabled) }
    }

    fun toggleFinanceDueDates(enabled: Boolean) {
        _state.update { it.copy(notifyFinanceDueDates = enabled) }
    }

    fun toggleChannelPush(enabled: Boolean) {
        _state.update { it.copy(channelPush = enabled) }
    }

    fun toggleChannelSms(enabled: Boolean) {
        _state.update { it.copy(channelSms = enabled) }
    }

    fun toggleChannelEmail(enabled: Boolean) {
        _state.update { it.copy(channelEmail = enabled) }
    }

    fun toggleBiometric(enabled: Boolean) {
        _state.update { it.copy(isBiometricEnabled = enabled) }
    }

    fun toggleQuickPin(enabled: Boolean) {
        _state.update { it.copy(isQuickPinEnabled = enabled) }
    }

    fun setLanguage(lang: String) {
        _state.update { it.copy(selectedLanguage = lang, successMessage = "Langue modifiée : $lang") }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _state.update { it.copy(isDarkMode = enabled) }
    }

    fun toggleDataSaver(enabled: Boolean) {
        _state.update { it.copy(isDataSaver = enabled) }
    }

    fun openLinkChildDialog() {
        _state.update { it.copy(isLinkingChildDialog = true) }
    }

    fun closeLinkChildDialog() {
        _state.update { it.copy(isLinkingChildDialog = false) }
    }

    fun linkNewChild(code: String, matricule: String, childName: String, className: String) {
        val newChild = AssociatedChild(
            id = (200..999).random().toLong(),
            matricule = matricule.ifBlank { "MAT-2026-${(1000..9999).random()}" },
            fullName = childName.ifBlank { "Nouvel Élève" },
            className = className.ifBlank { "1ère Primaire A" },
            schoolName = "Complexe Scolaire Kelasi Suite"
        )
        _state.update {
            it.copy(
                associatedChildren = it.associatedChildren + newChild,
                isLinkingChildDialog = false,
                successMessage = "L'élève ${newChild.fullName} a été rattaché à votre compte avec succès !"
            )
        }
    }

    fun openChangePinDialog() {
        _state.update { it.copy(isChangePinDialog = true) }
    }

    fun closeChangePinDialog() {
        _state.update { it.copy(isChangePinDialog = false) }
    }

    fun saveNewPin(newPin: String) {
        _state.update {
            it.copy(
                quickPinCode = newPin,
                isChangePinDialog = false,
                successMessage = "Code PIN d'autorisation mis à jour."
            )
        }
    }

    fun openChangePasswordDialog() {
        _state.update { it.copy(isChangePasswordDialog = true) }
    }

    fun closeChangePasswordDialog() {
        _state.update { it.copy(isChangePasswordDialog = false) }
    }

    fun saveNewPassword() {
        _state.update {
            it.copy(
                isChangePasswordDialog = false,
                successMessage = "Mot de passe modifié avec succès."
            )
        }
    }

    fun clearSuccessMessage() {
        _state.update { it.copy(successMessage = null) }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }
}
