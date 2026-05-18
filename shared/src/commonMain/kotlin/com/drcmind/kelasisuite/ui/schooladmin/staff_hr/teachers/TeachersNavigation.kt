package com.drcmind.kelasisuite.ui.schooladmin.staff_hr.teachers


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

@Composable
fun TeachersNavigation(
    modifier: Modifier = Modifier,
    teachersBackStack: NavBackStack<NavKey>
){
    NavDisplay(
        modifier = modifier,
        backStack = teachersBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.StaffHR.Teachers.ListDetails> {
                TeachersListDetailScreen(
                    onEditTeacher = {id->
                        teachersBackStack.add(Route.SchoolAdmin.StaffHR.Teachers.AddUpdate(id))
                    }
                )
            }
            entry<Route.SchoolAdmin.StaffHR.Teachers.ProfileDetails> {
                AcademicsScreen()
            }

            entry<Route.SchoolAdmin.StaffHR.Teachers.AddUpdate> {
                AddTeacherScreen(
                    teacherId = it.teacherId,
                    onBack = {
                        if (teachersBackStack.size > 1){
                            teachersBackStack.removeLastOrNull()
                        }
                    },
                    onTeacherAdded ={
                        if (teachersBackStack.size > 1) {
                            teachersBackStack.removeLast()
                        }
                    } ,
                )
            }
        }
    )
}