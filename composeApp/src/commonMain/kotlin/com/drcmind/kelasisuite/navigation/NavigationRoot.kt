package com.drcmind.kelasisuite.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.ui.SchoolAdminAppScreen
import com.drcmind.kelasisuite.ui.auth.AuthScreen
import com.drcmind.kelasisuite.ui.systemadmin.SystemAdminAppScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
){
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Auth::class, Route.Auth.serializer())
                    subclass(Route.SystemAdmin::class, Route.SystemAdmin.serializer())
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
                    onAuthSuccess = {
                        // Only add Home if it's not already the top route
                        if (rootBackStack.lastOrNull() != Route.SystemAdmin) {
                            rootBackStack.add(Route.SystemAdmin)
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