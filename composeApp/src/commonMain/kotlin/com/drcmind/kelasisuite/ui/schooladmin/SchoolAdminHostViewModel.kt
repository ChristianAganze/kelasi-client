package com.drcmind.kelasisuite.ui.schooladmin

import androidx.lifecycle.ViewModel
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.local.settings.UserInfo
import com.drcmind.kelasisuite.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SchoolAdminHostViewModel(private val settingsStorage: SettingsStorage) : ViewModel() {

    private val _currentRoute = MutableStateFlow<Route>(Route.SystemAdmin.Dashboard)
    val currentRoute: StateFlow<Route> = _currentRoute.asStateFlow()

    private val _userInfo = MutableStateFlow(settingsStorage.getUserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo.asStateFlow()

    fun updateRoute(route: Route) {
        _currentRoute.value = route
    }
}
