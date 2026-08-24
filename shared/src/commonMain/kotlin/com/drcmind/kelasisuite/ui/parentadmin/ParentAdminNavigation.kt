package com.drcmind.kelasisuite.ui.parentadmin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.parentadmin.children.ChildrenScreen
import com.drcmind.kelasisuite.ui.parentadmin.dashboard.ParentDashboardScreen
import com.drcmind.kelasisuite.ui.parentadmin.finance.FinanceScreen
import com.drcmind.kelasisuite.ui.parentadmin.settings.ParentSettingsScreen
import com.drcmind.kelasisuite.ui.teacheradmin.communication.CommunicationScreen

@Composable
fun ParentAdminNavigation(
    modifier: Modifier = Modifier,
    parentAdminBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        modifier = modifier,
        backStack = parentAdminBackStack,
        entryProvider = entryProvider {
            entry<Route.ParentAdmin.Dashboard> {
                ParentDashboardScreen()
            }
            entry<Route.ParentAdmin.Children> {
                ChildrenScreen()
            }
            entry<Route.ParentAdmin.Finance> {
                FinanceScreen()
            }
            entry<Route.ParentAdmin.Communication> {
                CommunicationScreen()
            }
            entry<Route.ParentAdmin.Settings> {
                ParentSettingsScreen()
            }
        }
    )
}
