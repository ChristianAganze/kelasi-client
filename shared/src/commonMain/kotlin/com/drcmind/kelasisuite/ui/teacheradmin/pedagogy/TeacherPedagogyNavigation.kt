package com.drcmind.kelasisuite.ui.teacheradmin.pedagogy

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.teacheradmin.preparation.PreparationScreen

@Composable
fun TeacherPedagogyNavigation(
    modifier: Modifier = Modifier,
    pedagogyBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        modifier = modifier,
        backStack = pedagogyBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.TeacherAdmin.Pedagogy.Preparations> {
                PreparationScreen()
            }
            entry<Route.TeacherAdmin.Pedagogy.AnnualDistribution> {
                AnnualDistributionScreen()
            }
            entry<Route.TeacherAdmin.Pedagogy.NationalCurriculum> {
                NationalCurriculumScreen()
            }
        }
    )
}
