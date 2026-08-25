package com.drcmind.kelasisuite.ui.teacheradmin.evaluations

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
fun TeacherEvaluationsNavigation(
    modifier: Modifier = Modifier,
    evaluationsBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        modifier = modifier,
        backStack = evaluationsBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.TeacherAdmin.Evaluations.GradeEntry> {
                GradeEntryScreen()
            }
            entry<Route.TeacherAdmin.Evaluations.ClassHeadmaster> {
                ClassHeadmasterScreen()
            }
            entry<Route.TeacherAdmin.Evaluations.OfficialBulletin> {
                OfficialRdcBulletinScreen()
            }
        }
    )
}
