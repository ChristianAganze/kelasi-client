package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

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

@Composable
fun SchoolStructureNavigation(
    modifier: Modifier = Modifier,
    schoolStructureBackStack: NavBackStack<NavKey>
){

    NavDisplay(
        modifier = modifier,
        backStack = schoolStructureBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.Academics.SchoolStructure.Structure> { key ->
                StructureScreen(
                    onAddClass = {
                        schoolStructureBackStack.add(Route.SchoolAdmin.Academics.SchoolStructure .AddClass())
                    },
                    onEditClass = { id ->
                        schoolStructureBackStack.add(Route.SchoolAdmin.Academics.SchoolStructure.AddClass(id))
                    },
                    onSelectClass = { id ->
                        schoolStructureBackStack.add(Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail(id))
                    }
                )
            }
            entry<Route.SchoolAdmin.Academics.SchoolStructure.AddClass> { key ->
                AddClassScreen(
                    classId = key.classId,
                    onBack = {
                        if (schoolStructureBackStack.size > 1) {
                            schoolStructureBackStack.removeLast()
                        }
                    },
                    onClassCreated = {
                        if (schoolStructureBackStack.size > 1) {
                            schoolStructureBackStack.removeLast()
                        }
                    }
                )
            }

            entry<Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail> { key ->
                // Placeholder for ClassDetailScreen
                Text("Détails de la classe ${key.classId}")
            }

        }
    )

}