package com.drcmind.kelasisuite.ui.schooladmin.academics


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.schooladmin.academics.calendar_periods.CalendarPeriodsScreen
import com.drcmind.kelasisuite.ui.schooladmin.academics.deliberation.DeliberationsConductScreen
import com.drcmind.kelasisuite.ui.schooladmin.academics.grading.EvaluationGradingScreen
import com.drcmind.kelasisuite.ui.schooladmin.academics.reports.ReportCardsScreen
import com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure.SchoolStructureScreen
import com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.StudentEnrollmentScreen

@Composable
fun AcademicsNavigation(
    modifier: Modifier = Modifier,
    academicsBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        modifier = modifier,
        backStack = academicsBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.Academics.CalendarPeriod> {
                CalendarPeriodsScreen()
            }
            entry<Route.SchoolAdmin.Academics.SchoolStructure> {
                SchoolStructureScreen()
            }

            entry<Route.SchoolAdmin.Academics.StudentEnrollment> {
                StudentEnrollmentScreen()
            }

            entry<Route.SchoolAdmin.Academics.EvaluationGrading> {
                EvaluationGradingScreen()
            }

            entry<Route.SchoolAdmin.Academics.DeliberationsConduct> {
                DeliberationsConductScreen()
            }

            entry<Route.SchoolAdmin.Academics.ReportCards> {
                ReportCardsScreen()
            }
        }
    )
}
