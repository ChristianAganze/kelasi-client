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
import com.drcmind.kelasisuite.ui.schooladmin.dashboard.SchoolDashboardScreen
import com.drcmind.kelasisuite.ui.schooladmin.parents.ParentsScreen
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.PedagogyScreen
import com.drcmind.kelasisuite.ui.schooladmin.profile.ProfileScreen
import com.drcmind.kelasisuite.ui.schooladmin.staff_hr.StaffHrScreen
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
                AcademicsScreen(
                    onNavigateToStudentDetail = { id ->
                        schoolAdminBackStack.add(Route.SchoolAdmin.StudentDetail(id))
                    },
                    onNavigateToTeacherDetail = { id ->
                        schoolAdminBackStack.add(Route.SchoolAdmin.TeacherDetail(id))
                    }
                )
            }

            entry<Route.SchoolAdmin.Pedagogy> {
                PedagogyScreen()
            }

            entry<Route.SchoolAdmin.Profile> {
                ProfileScreen()
            }

            entry<Route.SchoolAdmin.Students> {
                StudentsScreen()
            }

            entry<Route.SchoolAdmin.AddStudent> { key ->
                AddStudentScreen(
                    studentId = key.studentId,
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
                    showTopAppBar = true,
                    studentId = key.studentId,
                    onBack = {
                        if (schoolAdminBackStack.size > 1) {
                            schoolAdminBackStack.removeLast()
                        }
                    },
                    onEdit = { id ->
                        schoolAdminBackStack.add(Route.SchoolAdmin.AddStudent(id))
                    }
                )
            }

            entry<Route.SchoolAdmin.TeacherDetail> { key ->
                com.drcmind.kelasisuite.ui.schooladmin.staff_hr.teachers.TeacherDetailsScreen(
                    teacherId = key.teacherId,
                    onBack = {
                        if (schoolAdminBackStack.size > 1) {
                            schoolAdminBackStack.removeLast()
                        }
                    },
                    showTopAppBar = true,
                    onEdit = { id ->
                        schoolAdminBackStack.add(Route.SchoolAdmin.AddTeacher(id))
                    }
                )
            }

            entry<Route.SchoolAdmin.Parents> {
                ParentsScreen()
            }
            entry<Route.SchoolAdmin.StaffHR> {
                StaffHrScreen()
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