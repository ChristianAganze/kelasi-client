package com.drcmind.kelasisuite.ui.schooladmin.dashboard

import androidx.lifecycle.ViewModel
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SchoolDashboardState(
    val username: String = "Administrator",
    val role: String = "Core Admin",
    val systemStatus: String = "Opérationnel",
    val lastConnection: String = "Aujourd'hui, 09:41"
)

class SchoolDashboardViewModel(
    private val settingsStorage: SettingsStorage
) : ViewModel() {
    private val userInfo = settingsStorage.getUserInfo()
    private val _state = MutableStateFlow(
        SchoolDashboardState(
            username = userInfo.preferredFirstName.ifBlank { "Administrateur" },
            role = userInfo.role ?: "Core Admin"
        )
    )
    val state: StateFlow<SchoolDashboardState> = _state.asStateFlow()
}
