package com.drcmind.kelasisuite.navigation

import com.drcmind.kelasisuite.ui.schooladmin.SchoolAdminAppScreen
import SystemAdminAppScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.ui.auth.AuthScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Auth::class, Route.Auth.serializer())
                    subclass(Route.SystemAdmin::class, Route.SystemAdmin.serializer())
                    subclass(Route.SchoolAdmin::class, Route.SchoolAdmin.serializer())
                }
            }
        },
        Route.Auth
    )

    NavDisplay(
        modifier = modifier,
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Auth> {
                AuthScreen(
                    onAuthSuccess = { role ->
                        val nextRoute = when {
                            role.contains("ROLE_SUPER_USER", ignoreCase = true) -> Route.SystemAdmin
                            role.contains(
                                "ROLE_SCHOOL_ADMIN",
                                ignoreCase = true
                            ) -> Route.SchoolAdmin

                            else -> Route.SchoolAdmin
                        }

                        if (rootBackStack.lastOrNull() != nextRoute) {
                            rootBackStack.add(nextRoute)
                        }
                    }
                )
            }
            entry<Route.SystemAdmin> {
                SystemAdminAppScreen()
            }
            entry<Route.SchoolAdmin> {
                SchoolAdminAppScreen()
            }
        }
    )
}