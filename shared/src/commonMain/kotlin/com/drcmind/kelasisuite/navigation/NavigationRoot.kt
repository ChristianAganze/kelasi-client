package com.drcmind.kelasisuite.navigation

import com.drcmind.kelasisuite.ui.schooladmin.SchoolAdminAppScreen
import com.drcmind.kelasisuite.ui.systemadmin.SystemAdminAppScreen
import com.drcmind.kelasisuite.ui.teacheradmin.TeacherAdminAppScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.AppViewModel
import com.drcmind.kelasisuite.ui.LoadingScreen
import com.drcmind.kelasisuite.ui.auth.AuthScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = koinViewModel()
) {
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Loading::class, Route.Loading.serializer())
                    subclass(Route.Auth::class, Route.Auth.serializer())
                    subclass(Route.SystemAdmin::class, Route.SystemAdmin.serializer())
                    subclass(Route.SchoolAdmin::class, Route.SchoolAdmin.serializer())
                    subclass(Route.TeacherAdmin::class, Route.TeacherAdmin.serializer())
                    subclass(Route.ParentAdmin::class, Route.ParentAdmin.serializer())
                }
            }
        },
        Route.Loading
    )

    val navigationState by appViewModel.navigationState.collectAsState()

    LaunchedEffect(navigationState){
        if (navigationState == Route.Loading) return@LaunchedEffect

        val targetRoute = navigationState

        if (rootBackStack.isNotEmpty()) {
            if (rootBackStack[0] != targetRoute) {
                rootBackStack.set(0, targetRoute)
            }

            while (rootBackStack.size > 1) {
                rootBackStack.removeLast()
            }
        } else {
            rootBackStack.add(targetRoute)
        }
    }

    NavDisplay(
        modifier = modifier,
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Loading> {
                LoadingScreen()
            }
            entry<Route.Auth> {
                AuthScreen(
                    onAuthSuccess = { role ->
                        val nextRoute = when {
                            role.contains(
                                "ROLE_SUPER_USER",
                                ignoreCase = true
                            ) -> Route.SystemAdmin
                            role.contains(
                                "ROLE_SCHOOL_ADMIN",
                                ignoreCase = true
                            ) -> Route.SchoolAdmin
                            role.contains(
                                "ROLE_TEACHER",
                                ignoreCase = true
                            ) -> Route.TeacherAdmin

                            else -> Route.SchoolAdmin
                        }

                        appViewModel.setNavigationState(nextRoute)
                    }
                )
            }
            entry<Route.SystemAdmin> {
                SystemAdminAppScreen(
                    onLogout = {appViewModel.logout()}
                )
            }
            entry<Route.SchoolAdmin> {
                SchoolAdminAppScreen(
                    onLogout = {appViewModel.logout()}
                )
            }
            entry<Route.TeacherAdmin> {
                TeacherAdminAppScreen(
                    onLogout = {appViewModel.logout()}
                )
            }
        }
    )
}