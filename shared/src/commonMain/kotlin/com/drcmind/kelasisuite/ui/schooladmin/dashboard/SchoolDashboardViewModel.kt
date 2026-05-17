package com.drcmind.kelasisuite.ui.schooladmin.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SchoolDashboardState(
    val username: String = "Administrator",
    val role: String = "Core Admin",
    val systemStatus: String = "Opérationnel",
    val lastConnection: String = "Aujourd'hui, 09:41"
)

class SchoolDashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(SchoolDashboardState())
    val state: StateFlow<SchoolDashboardState> = _state.asStateFlow()
}
