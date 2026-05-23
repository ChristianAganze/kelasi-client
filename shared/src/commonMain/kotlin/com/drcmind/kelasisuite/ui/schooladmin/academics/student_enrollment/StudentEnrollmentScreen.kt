package com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment

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
import com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.enrollment.EnrollmentScreen
import com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.student.StudentsScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun StudentEnrollmentScreen(){
    val studentEnrollmentBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment::class,
                        Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.StudentEnrollment.Students::class,
                        Route.SchoolAdmin.Academics.StudentEnrollment.Students.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment
    )
    var selectedDestination by rememberSaveable {
        mutableStateOf(Route.SchoolAdmin.Academics.StudentEnrollment.TabDestination.ENROLLMENT.ordinal)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SecondaryTabRow(selectedTabIndex = selectedDestination, containerColor = Color.Transparent) {
            Route.SchoolAdmin.Academics.StudentEnrollment.TabDestination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        studentEnrollmentBackStack.add(destination.route)
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
            backStack = studentEnrollmentBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment> {
                    EnrollmentScreen()
                }
                entry<Route.SchoolAdmin.Academics.StudentEnrollment.Students> {
                    StudentsScreen()
                }
            }
        )
    }

}