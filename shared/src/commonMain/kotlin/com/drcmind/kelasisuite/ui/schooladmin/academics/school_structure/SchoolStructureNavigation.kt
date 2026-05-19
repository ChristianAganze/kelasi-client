package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.drcmind.kelasisuite.navigation.Route
import org.koin.core.component.getScopeName

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
                    schoolStructureBackStack = schoolStructureBackStack
                )
            }
            entry<Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail> { key ->
                ClassDetailsScreen(
                    classId = key.classId,
                    className = key.className,
                    onBack = {
                        if (schoolStructureBackStack.size > 1) {
                            schoolStructureBackStack.removeLast()
                        }
                    }
                )
            }
        }
    )

}