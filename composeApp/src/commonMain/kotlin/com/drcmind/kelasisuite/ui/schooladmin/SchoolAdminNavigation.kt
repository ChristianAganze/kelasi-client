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
import com.drcmind.kelasisuite.ui.schooladmin.academics.AcademicsScreen
import com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure.AddClassScreen
import com.drcmind.kelasisuite.ui.schooladmin.dashboard.SchoolDashboardScreen
import com.drcmind.kelasisuite.ui.schooladmin.profile.ProfileScreen
import com.drcmind.kelasisuite.ui.schooladmin.students.AddStudentScreen
import com.drcmind.kelasisuite.ui.schooladmin.students.StudentDetailScreen
import com.drcmind.kelasisuite.ui.schooladmin.students.StudentsScreen

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
                AcademicsScreen()
            }

            entry<Route.SchoolAdmin.Pedagogy> {
                Text("Pédagogie")
            }

            entry<Route.SchoolAdmin.Profile> {
                ProfileScreen()
            }

            entry<Route.SchoolAdmin.Students> {
                StudentsScreen(

                    onNavigateToAddStudent = {
                        schoolAdminBackStack.add(Route.SchoolAdmin.AddStudent)
                    },
                    onNavigateToStudentDetail = { id ->
                        schoolAdminBackStack.add(Route.SchoolAdmin.StudentDetail(id))
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
                    onStudentAdded = {
                        if (schoolAdminBackStack.size > 1) {
                            schoolAdminBackStack.removeLast()
                        }
                    }
                )
            }

            entry<Route.SchoolAdmin.StudentDetail> { key ->
                StudentDetailScreen(
                    studentId = key.studentId,
                    onBack = {
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