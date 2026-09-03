package com.drcmind.kelasisuite.ui.teacheradmin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.teacheradmin.classlog.TeacherClassLogScreen
import com.drcmind.kelasisuite.ui.teacheradmin.communication.CommunicationScreen
import com.drcmind.kelasisuite.ui.teacheradmin.dashboard.TeacherDashboardScreen
import com.drcmind.kelasisuite.ui.teacheradmin.evaluations.TeacherEvaluationsScreen
import com.drcmind.kelasisuite.ui.teacheradmin.pedagogy.TeacherPedagogyScreen
import com.drcmind.kelasisuite.ui.teacheradmin.settings.TeacherSettingsScreen

@Composable
fun TeacherAdminNavigation(
    modifier: Modifier = Modifier,
    teacherAdminBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        modifier = modifier,
        backStack = teacherAdminBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.TeacherAdmin.Dashboard> {
                TeacherDashboardScreen(
                    onNavigate = { key -> teacherAdminBackStack.add(key) }
                )
            }
            entry<Route.TeacherAdmin.Pedagogy> {
                TeacherPedagogyScreen()
            }
            entry<Route.TeacherAdmin.ClassLog> {
                TeacherClassLogScreen()
            }
            entry<Route.TeacherAdmin.Evaluations> {
                TeacherEvaluationsScreen()
            }
            entry<Route.TeacherAdmin.Communication> {
                CommunicationScreen()
            }
            entry<Route.TeacherAdmin.Settings> {
                TeacherSettingsScreen()
            }
        }
    )
}
