package com.drcmind.kelasisuite.ui.schooladmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
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
fun SchoolAdminAppScreen(
    onLogout: () -> Unit
) {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val layoutType = with(adaptiveInfo) {
        if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            NavigationSuiteType.NavigationDrawer
        } else {
            NavigationSuiteType.WideNavigationRailExpanded
        }
    }

    val schoolAdminBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.SchoolDashboard::class,
                        Route.SchoolAdmin.SchoolDashboard.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics::class,
                        Route.SchoolAdmin.Academics.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy::class,
                        Route.SchoolAdmin.Pedagogy.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Students::class,
                        Route.SchoolAdmin.Students.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Parents::class,
                        Route.SchoolAdmin.Parents.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.StaffHR::class,
                        Route.SchoolAdmin.StaffHR.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Finance::class,
                        Route.SchoolAdmin.Finance.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Logistics::class,
                        Route.SchoolAdmin.Logistics.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Communication::class,
                        Route.SchoolAdmin.Communication.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Settings::class,
                        Route.SchoolAdmin.Settings.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.AddStudent::class,
                        Route.SchoolAdmin.AddStudent.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Profile::class,
                        Route.SchoolAdmin.Profile.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.SchoolDashboard
    )

    var currentKey by rememberSaveable(stateSaver = Route.SchoolAdmin.stateSaver) {
        mutableStateOf(Route.SchoolAdmin.SchoolDashboard)
    }

    var showMenu by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kelasi School Admin",
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                        Icon(imageVector = Icons.Filled.School, contentDescription = "logo")
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

                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = "Notifications",
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
                                            Icons.Default.AccountCircle,
                                            contentDescription = null
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text("Mon Profil")
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    schoolAdminBackStack.add(Route.SchoolAdmin.Profile)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                            contentDescription = null
                                        )
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
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            modifier = Modifier.padding(innerPadding),
            navigationSuiteItems = {
                Route.SchoolAdmin.items.forEach { item ->
                    item(
                        selected = item == currentKey,
                        onClick = {
                            if (currentKey != item) {
                                currentKey = item
                                schoolAdminBackStack.add(item)
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }

                    )
                }
            },
            layoutType = layoutType
        ) {
            SchoolAdminNavigation(schoolAdminBackStack = schoolAdminBackStack)
        }
    }
}