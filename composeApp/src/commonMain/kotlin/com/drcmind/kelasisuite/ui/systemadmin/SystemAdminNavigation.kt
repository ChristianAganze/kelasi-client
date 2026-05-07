package com.drcmind.kelasisuite.ui.systemadmin

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
fun SystemAdminNavigation(
    modifier: Modifier = Modifier,
    systemAdminBackStack : NavBackStack<NavKey>
){
    NavDisplay(
        modifier = modifier,
        backStack = systemAdminBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.SystemAdmin.Dashboard> {
                Text("Profile")
            }
            entry<Route.SystemAdmin.Curriculum> {
                Text("Profile")
            }
            entry<Route.SystemAdmin.Subjects> {
                Text("Setting")
            }

            entry<Route.SystemAdmin.Schools> {
                Text("Setting")
            }
            entry<Route.SystemAdmin.Templates> {
                Text("Setting")
            }
            entry<Route.SystemAdmin.Settings> {
                Text("Setting")
            }
        }
    )
}