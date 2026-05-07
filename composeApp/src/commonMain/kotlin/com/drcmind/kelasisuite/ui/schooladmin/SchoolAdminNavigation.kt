package com.drcmind.kelasisuite.ui.schooladmin

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.schooladmin.Dashboard.SchoolDashboardScreen
import com.drcmind.kelasisuite.ui.schooladmin.Students.AddStudentScreen
import com.drcmind.kelasisuite.ui.schooladmin.Students.StudentsScreen
import com.drcmind.kelasisuite.ui.schooladmin.Students.StudentsViewModel

@Composable
fun SchoolAdminNavigation(
    modifier: Modifier = Modifier,
    schoolAdminBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        modifier = modifier,
        backStack = schoolAdminBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.SchoolDashboard> {
                SchoolDashboardScreen()
            }
            entry<Route.SchoolAdmin.Academics> {
                Text("Affaires académiques")
            }

            entry<Route.SchoolAdmin.Students> {
                StudentsScreen(
                    viewModel = StudentsViewModel(),
                    onBack = {
                        if (schoolAdminBackStack.size > 1) {
                            schoolAdminBackStack.removeLast()
                        }
                    },
                    onNavigateToAddStudent = {
                        schoolAdminBackStack.add(Route.SchoolAdmin.AddStudent)
                    },
                )
            }

            entry<Route.SchoolAdmin.AddStudent> {
                AddStudentScreen(
                    onBack = {
                        if (schoolAdminBackStack.size > 1) {
                            schoolAdminBackStack.removeLast()
                        }
                    },

                    onConfirm = {
                        if (schoolAdminBackStack.size > 1) {
                            schoolAdminBackStack.removeLast()
                        }
                    }
                )
            }

            entry<Route.SchoolAdmin.Parents> {
                Text("Parents")
            }
            entry<Route.SchoolAdmin.StaffHR> {
                Text("Staffs & HR")
            }
            entry<Route.SchoolAdmin.Finance> {
                Text("Finance & Comptabilité")
            }
            entry<Route.SchoolAdmin.Logistics> {
                Text("Logistique & Opérations")
            }
            entry<Route.SchoolAdmin.Communication> {
                Text("Communicationn")
            }
            entry<Route.SchoolAdmin.Settings> {
                Text("Paramètres")
            }
        }
    )
}