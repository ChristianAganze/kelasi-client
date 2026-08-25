package com.drcmind.kelasisuite.ui.teacheradmin.classlog

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
fun TeacherClassLogNavigation(
    modifier: Modifier = Modifier,
    classLogBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        modifier = modifier,
        backStack = classLogBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.TeacherAdmin.ClassLog.NewEntry> {
                ClassLogScreen()
            }
            entry<Route.TeacherAdmin.ClassLog.History> {
                ClassLogHistoryScreen()
            }
        }
    )
}
