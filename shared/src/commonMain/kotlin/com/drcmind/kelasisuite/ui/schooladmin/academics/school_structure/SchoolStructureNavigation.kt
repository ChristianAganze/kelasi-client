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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolStructureNavigation(
    modifier: Modifier = Modifier,
    schoolStructureBackStack: NavBackStack<NavKey>,
    onNavigateToStudentDetail: (Long) -> Unit = {},
    viewModel: SchoolStructureViewModel = koinViewModel(),
    onNavigateToTeacherDetail: (Long) -> Unit = {}
) {
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
                viewModel.loadPendingTeachingAssignments(key.classId)
                viewModel.loadClassTeachingAssignments(key.classId)
                ClassDetailsScreen(
                    viewModel = viewModel,
                    classId = key.classId,
                    className = key.className,
                    onBack = {
                        if (schoolStructureBackStack.size > 1) {
                            schoolStructureBackStack.removeLast()
                        }
                    },
                    onNavigateToStudentDetail = onNavigateToStudentDetail,
                    onNavigateToTeacherDetail = onNavigateToTeacherDetail
                )
            }
        }
    )

}