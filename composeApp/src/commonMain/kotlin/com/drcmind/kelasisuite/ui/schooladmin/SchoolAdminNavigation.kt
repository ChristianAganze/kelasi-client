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
import com.drcmind.kelasisuite.ui.schooladmin.academicManagement.AcademicManagementScreen
import com.drcmind.kelasisuite.ui.schooladmin.academicManagement.AddClassScreen
import com.drcmind.kelasisuite.ui.schooladmin.dashboard.SchoolDashboardScreen
import com.drcmind.kelasisuite.ui.schooladmin.profile.ProfileScreen
import com.drcmind.kelasisuite.ui.schooladmin.students.AddStudentScreen
import com.drcmind.kelasisuite.ui.schooladmin.students.StudentDetailScreen
import com.drcmind.kelasisuite.ui.schooladmin.students.StudentsScreen
import com.drcmind.kelasisuite.ui.schooladmin.teachers.AddTeacherScreen
import com.drcmind.kelasisuite.ui.schooladmin.teachers.TeachersScreen

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
                AcademicManagementScreen(
                    onAddClass = {
                        schoolAdminBackStack.add(Route.SchoolAdmin.AddClass())
                    },
                    onEditClass = { id ->
                        schoolAdminBackStack.add(Route.SchoolAdmin.AddClass(id))
                    },
                    onSelectClass = { id ->
                        schoolAdminBackStack.add(Route.SchoolAdmin.ClassDetail(id))
                    }
                )
            }

            entry<Route.SchoolAdmin.AddClass> { key ->
                AddClassScreen(
                    classId = key.classId,
                    onBack = {
                        if (schoolAdminBackStack.size > 1) {
                            schoolAdminBackStack.removeLast()
                        }
                    },
                    onClassCreated = {
                        if (schoolAdminBackStack.size > 1) {
                            schoolAdminBackStack.removeLast()
                        }
                    }
                )
            }

            entry<Route.SchoolAdmin.ClassDetail> { key ->
                // Placeholder for ClassDetailScreen
                Text("Détails de la classe ${key.classId}")
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
                TeachersScreen(
                    onNavigateToAddTeacher = {
                        schoolAdminBackStack.add(Route.SchoolAdmin.AddTeacher)
                    },
                    onNavigateToTeacherDetail = {
//                        id ->
//                        schoolAdminBackStack.add(Route.SchoolAdmin.StudentDetail(id))
                    },
                )
            }

            entry<Route.SchoolAdmin.AddTeacher> {
                AddTeacherScreen(
                    onBack = {
                        if (schoolAdminBackStack.size > 1) {
                            schoolAdminBackStack.removeLast()
                        }
                    },
                    onTeacherAdded = {
                        if (schoolAdminBackStack.size > 1) {
                            schoolAdminBackStack.removeLast()
                        }
                    }
                )
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