package com.drcmind.kelasisuite.ui.parentadmin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.drcmind.kelasisuite.navigation.Route
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ParentAdminAppScreen(
    onLogout: () -> Unit
) {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val layoutType = with(adaptiveInfo) {
        if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }
    }

    val parentAdminBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.ParentAdmin.Dashboard::class, Route.ParentAdmin.Dashboard.serializer())
                    subclass(Route.ParentAdmin.Children::class, Route.ParentAdmin.Children.serializer())
                    subclass(Route.ParentAdmin.Finance::class, Route.ParentAdmin.Finance.serializer())
                    subclass(Route.ParentAdmin.Communication::class, Route.ParentAdmin.Communication.serializer())
                }
            }
        },
        Route.ParentAdmin.Dashboard
    )

    var currentKey by rememberSaveable(stateSaver = Route.ParentAdmin.stateSaver) {
        mutableStateOf(Route.ParentAdmin.Dashboard)
    }

    var showMenu by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Kelasi Parent")
                },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                        Icon(imageVector = Icons.Default.FamilyRestroom, contentDescription = "logo")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Box {
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = "Help",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                            contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Déconnexion")
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavigationSuiteScaffold(
            modifier = Modifier.padding(innerPadding),
            navigationSuiteItems = {
                Route.ParentAdmin.items.forEach { item ->
                    item(
                        selected = item == currentKey,
                        onClick = {
                            if (currentKey != item) {
                                currentKey = item
                                parentAdminBackStack.add(item)
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        alwaysShowLabel = false
                    )
                }
            },
            layoutType = layoutType
        ) {
            ParentAdminNavigation(parentAdminBackStack = parentAdminBackStack)
        }
    }
}
