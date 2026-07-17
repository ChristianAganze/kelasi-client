package com.drcmind.kelasisuite.ui.teacheradmin

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
import com.drcmind.kelasisuite.ui.teacheradmin.classlog.ClassLogScreen
import com.drcmind.kelasisuite.ui.teacheradmin.dashboard.TeacherDashboardScreen
import com.drcmind.kelasisuite.ui.teacheradmin.dashboard.TeacherDashboardScreen
import com.drcmind.kelasisuite.ui.teacheradmin.schedule.TeacherScheduleScreen
import com.drcmind.kelasisuite.ui.teacheradmin.classes.ClassesScreen
import com.drcmind.kelasisuite.ui.teacheradmin.communication.CommunicationScreen
import com.drcmind.kelasisuite.ui.teacheradmin.reports.ReportsScreen
import com.drcmind.kelasisuite.ui.teacheradmin.preparation.PreparationScreen

@Composable
fun TeacherAdminNavigation(
    modifier: Modifier = Modifier,
    teacherAdminBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        modifier = modifier,
        backStack = teacherAdminBackStack,
        entryProvider = entryProvider {
            entry<Route.TeacherAdmin.Dashboard> {
                TeacherDashboardScreen()
            }
            entry<Route.TeacherAdmin.Preparation> {
                PreparationScreen()
            }
            entry<Route.TeacherAdmin.ClassLog> {
                ClassLogScreen()
            }
            entry<Route.TeacherAdmin.Schedule> {
                TeacherScheduleScreen()
            }
            entry<Route.TeacherAdmin.Classes> {
                ClassesScreen()
            }
            entry<Route.TeacherAdmin.Communication> {
                CommunicationScreen()
            }
            entry<Route.TeacherAdmin.Reports> {
                ReportsScreen()
            }
        }
    )
}
