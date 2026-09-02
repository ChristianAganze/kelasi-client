package com.drcmind.kelasisuite.ui.teacheradmin

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
    ExperimentalMaterial3AdaptiveApi::class
)
fun TeacherAdminAppScreen(
    viewModel: TeacherAdminViewModel = koinViewModel(),
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = with(adaptiveInfo) {
        if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            NavigationSuiteType.WideNavigationRailExpanded
        } else if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {

            NavigationSuiteType.NavigationRail
        } else {

            NavigationSuiteType.NavigationBar
        }
    }

    val teacherAdminBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.TeacherAdmin.Dashboard::class, Route.TeacherAdmin.Dashboard.serializer())
                    subclass(Route.TeacherAdmin.Pedagogy::class, Route.TeacherAdmin.Pedagogy.serializer())
                    subclass(Route.TeacherAdmin.ClassLog::class, Route.TeacherAdmin.ClassLog.serializer())
                    subclass(Route.TeacherAdmin.Evaluations::class, Route.TeacherAdmin.Evaluations.serializer())
                    subclass(Route.TeacherAdmin.Communication::class, Route.TeacherAdmin.Communication.serializer())
                    subclass(Route.TeacherAdmin.Settings::class, Route.TeacherAdmin.Settings.serializer())
                }
            }
        },
        Route.TeacherAdmin.Dashboard
    )

    var currentKey by rememberSaveable(stateSaver = Route.TeacherAdmin.stateSaver) {
        mutableStateOf(Route.TeacherAdmin.Dashboard)
    }

    var showMenu by rememberSaveable { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                title = {
                    Text(
                        text = "Kelasi Teacher",
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end =8.dp)) {
                        Icon(imageVector = Icons.Filled.School, contentDescription = "logo")
                    }
                },
                actions = {
                    Box(modifier = Modifier) {
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
                            Text(uiState.activeAcademicYear?.label ?: "2025-2026")
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
                            if (uiState.academicYears.isNotEmpty()) {
                                uiState.academicYears.forEach { academicYear ->
                                    DropdownMenuItem(
                                        text = { Text(academicYear.label) },
                                        onClick = {
                                            viewModel.selectAcademicYear(academicYear)
                                            checked = false
                                        },
                                        leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                                    )
                                }
                            } else {
                                DropdownMenuItem(
                                    text = { Text("2025-2026 (Actif)") },
                                    onClick = { checked = false },
                                    leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Consulter calendrier") },
                                onClick = { checked = false },
                                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
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
                            contentDescription = "Facilitation & Aide",
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
                                        Text("Mon Profil Enseignant")
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    if (currentKey != Route.TeacherAdmin.Settings) {
                                        currentKey = Route.TeacherAdmin.Settings
                                        teacherAdminBackStack.add(Route.TeacherAdmin.Settings)
                                    }
                                }
                            )
                            HorizontalDivider()
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
                ),
                navigationRailContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                navigationBarContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            ),
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
                        modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                    )
                }
            },
            layoutType = layoutType
        ) {
            TeacherAdminNavigation(teacherAdminBackStack = teacherAdminBackStack)
        }
    }
}
