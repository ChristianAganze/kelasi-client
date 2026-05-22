package com.drcmind.kelasisuite.ui.schooladmin.students

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.drcmind.kelasisuite.navigation.Route

@Composable
fun StudentsNavigation(
    modifier: Modifier = Modifier,
    studentsBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        modifier = modifier,
        backStack = studentsBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.Students.ListDetails> {
                StudentsListDetailScreen(
                    onEditStudent = { id ->
                        studentsBackStack.add(Route.SchoolAdmin.AddStudent(id))
                    }
                )
            }

            entry<Route.SchoolAdmin.AddStudent> { key ->
                AddStudentScreen(
                    studentId = key.studentId,
                    onBack = {
                        if (studentsBackStack.size > 1) {
                            studentsBackStack.removeLast()
                        }
                    },
                    onStudentAdded = {
                        if (studentsBackStack.size > 1) {
                            studentsBackStack.removeLast()
                        }
                    }
                )
            }
        }
    )
}
