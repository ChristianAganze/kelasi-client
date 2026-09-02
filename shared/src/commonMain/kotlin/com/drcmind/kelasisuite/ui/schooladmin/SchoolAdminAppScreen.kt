package com.drcmind.kelasisuite.ui.schooladmin

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.drcmind.kelasisuite.navigation.Route
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
fun SchoolAdminAppScreen(
    viewModel: SchoolAdminViewModel = koinViewModel(),
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = with(adaptiveInfo) {
        if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            // Desktop (JVM) / Web : Navigation Rail étendu avec Icône et Libellé côte à côte
            NavigationSuiteType.WideNavigationRailExpanded
        } else if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            // Tablette : Navigation Rail standard avec Icône et Libellé empilés en colonne
            NavigationSuiteType.NavigationRail
        } else {
            // Smartphone : Barre de navigation inférieure
            NavigationSuiteType.NavigationBar
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
    var checked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
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
                    Box(modifier = Modifier){
                        OutlinedButton(
                            onClick = { checked = !checked },
                            modifier = Modifier.semantics {
                                stateDescription = if (checked) "Expanded" else "Collapsed"
                                contentDescription = "Menu de sélection d'année académique"
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(uiState.activeAcademicYear?.label ?: "Pas définie")
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            val rotation: Float by animateFloatAsState(
                                targetValue = if (checked) 180f else 0f,
                                label = "Trailing Icon Rotation"
                            )
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ButtonDefaults.IconSize)
                                    .graphicsLayer { this.rotationZ = rotation }
                            )
                        }
                        DropdownMenu(expanded = checked, onDismissRequest = { checked = false }) {
                            uiState.academicYears.forEach { academicYear->
                                DropdownMenuItem(
                                    text = { Text(academicYear.label) },
                                    onClick = {
                                        viewModel.selectAcademicYear(academicYear)
                                        checked = false
                                              },
                                    leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Demander nouvelle AS") },
                                onClick = { /* Handle send feedback! */ },
                                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                                trailingIcon = { Text("F11", textAlign = TextAlign.Center) },
                            )
                        }
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Help,
                            contentDescription = "Facilitation",
                            modifier = Modifier.size(32.dp)
                        )
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
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                wideNavigationRailColors = WideNavigationRailDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                )
            ),
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
                        label = { Text(item.label) },
                        modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                    )
                }
            },
            layoutType = layoutType,
        ) {
            SchoolAdminNavigation(schoolAdminBackStack = schoolAdminBackStack)
        }
    }
}
