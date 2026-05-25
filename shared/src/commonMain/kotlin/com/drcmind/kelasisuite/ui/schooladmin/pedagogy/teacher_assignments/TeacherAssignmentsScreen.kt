package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teachers.TeachersScreen
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teaching_assignment.TeachingAssignmentScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun TeacherAssignmentsScreen(){
    val teacherAssignmentsBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment::class,
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers::class,
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment
    )
    var selectedDestination by rememberSaveable {
        mutableStateOf(Route.SchoolAdmin.Pedagogy.TeacherAssignments.TabDestination.TEACHING_ASSIGNMENT.ordinal)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SecondaryTabRow(selectedTabIndex = selectedDestination, containerColor = Color.Transparent) {
            Route.SchoolAdmin.Pedagogy.TeacherAssignments.TabDestination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        teacherAssignmentsBackStack.add(destination.route)
                        selectedDestination = index
                    },
                    text = {
                        Text(
                            text = destination.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        NavDisplay(
            backStack = teacherAssignmentsBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment> {
                    TeachingAssignmentScreen()
                }
                entry<Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers> {
                    TeachersScreen()
                }
            }
        )
    }
}