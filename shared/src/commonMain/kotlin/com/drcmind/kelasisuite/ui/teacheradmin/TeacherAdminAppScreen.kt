package com.drcmind.kelasisuite.ui.teacheradmin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
fun TeacherAdminAppScreen(
    onLogout: () -> Unit
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = with(adaptiveInfo) {
        if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            NavigationSuiteType.WideNavigationRailExpanded
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }
    }

    val isCompact = layoutType == NavigationSuiteType.NavigationBar

    val teacherAdminBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.TeacherAdmin.Dashboard::class, Route.TeacherAdmin.Dashboard.serializer())
                    subclass(Route.TeacherAdmin.Preparation::class, Route.TeacherAdmin.Preparation.serializer())
                    subclass(Route.TeacherAdmin.ClassLog::class, Route.TeacherAdmin.ClassLog.serializer())
                    subclass(Route.TeacherAdmin.Schedule::class, Route.TeacherAdmin.Schedule.serializer())
                    subclass(Route.TeacherAdmin.Classes::class, Route.TeacherAdmin.Classes.serializer())
                    subclass(Route.TeacherAdmin.Communication::class, Route.TeacherAdmin.Communication.serializer())
                    subclass(Route.TeacherAdmin.Reports::class, Route.TeacherAdmin.Reports.serializer())
                }
            }
        },
        Route.TeacherAdmin.Dashboard
    )

    var currentKey by rememberSaveable(stateSaver = Route.TeacherAdmin.stateSaver) {
        mutableStateOf(Route.TeacherAdmin.Dashboard)
    }

    var showMenu by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Kelasi Teacher")
                },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                        Icon(imageVector = Icons.Filled.School, contentDescription = "logo")
                    }

                },
                actions = {
                    IconButton(onClick = {

                    }) {
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
                Route.TeacherAdmin.items.forEach { item ->
                    item(
                        selected = item == currentKey,
                        onClick = {
                            if (currentKey != item) {
                                currentKey = item
                                teacherAdminBackStack.add(item)
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
            TeacherAdminNavigation(teacherAdminBackStack = teacherAdminBackStack)
        }
    }
}
