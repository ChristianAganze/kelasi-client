package com.drcmind.kelasisuite.ui.schooladmin


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.util.AdaptiveUtil
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppColors
import com.drcmind.kelasisuite.ui.components.AppIcons
import com.drcmind.kelasisuite.ui.schooladmin.AcademicManagement.AddClassScreen
import com.drcmind.kelasisuite.ui.schooladmin.AcademicManagement.ClassDetailsScreen
import com.drcmind.kelasisuite.ui.schooladmin.AcademicManagement.HomeScreen
import com.drcmind.kelasisuite.ui.schooladmin.Dashboard.SchoolDashboardScreen
import org.koin.compose.koinInject


@Composable
fun SchoolAdminAppScreen(
    viewModel: SchoolAdminHostViewModel = koinInject()
) {
    val currentRoute by viewModel.currentRoute.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()

    val navItems = listOf(
        SidebarItem("Tableau de bord", AppIcons.dashboard, Route.SchoolAdmin.Admin.Home),
        SidebarItem("Programme", AppIcons.curriculum, Route.SchoolAdmin.Admin.Programme),
        SidebarItem("Inscriptions", AppIcons.enrollment, Route.SchoolAdmin.Admin.Inscriptions),
        SidebarItem("Finances", AppIcons.financial, Route.SchoolAdmin.Admin.Finances),
        SidebarItem("Communication", AppIcons.communication, Route.SchoolAdmin.Admin.Communication)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val with = maxWidth

        if (!AdaptiveUtil.isMedium(with) && !AdaptiveUtil.isCompact(with)) {
             Row(modifier = Modifier.fillMaxSize()) {
                Sidebar(
                    navItems = navItems,
                    currentRoute = currentRoute,
                    onRouteSelected = { viewModel.updateRoute(it) },
                    username = userInfo.username,
                    role = userInfo.roles,
                    modifier = Modifier.width(260.dp).fillMaxHeight()
                )

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    MainContentArea(
                        currentRoute,
                        onAcademicOpen = { viewModel.updateRoute(Route.SchoolAdmin.Admin.AcademicHome) },
                        onAcademicBack = { viewModel.updateRoute(Route.SchoolAdmin.Admin.Home) },
                        onAddClass = { viewModel.updateRoute(Route.SchoolAdmin.Admin.AcademicAdd) },
                        onSelectClass = { viewModel.updateRoute(Route.SchoolAdmin.Admin.ClassDetails(it)) },
                        onClassCreated = { viewModel.updateRoute(Route.SchoolAdmin.Admin.AcademicHome) }
                    )
                }
            }
        } else {
             Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        navItems.forEach { item ->
                            val isSelected = currentRoute == item.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.updateRoute(item.route) },
                                icon = {
                                    Icon(item.icon, contentDescription = item.label)
                                },
                                label = {
                                    Text(item.label, fontSize = 12.sp, textAlign = TextAlign.Center)
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AppColors.primary,
                                    selectedTextColor = AppColors.primary,
                                    unselectedIconColor = AppColors.onSurfaceVariant,
                                    indicatorColor = AppColors.surfaceContainerLow
                                )
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    MainContentArea(
                        currentRoute,
                        onAcademicOpen = { viewModel.updateRoute(Route.SchoolAdmin.Admin.AcademicHome) },
                        onAcademicBack = { viewModel.updateRoute(Route.SchoolAdmin.Admin.Home) },
                        onAddClass = { viewModel.updateRoute(Route.SchoolAdmin.Admin.AcademicAdd) },
                        onSelectClass = { viewModel.updateRoute(Route.SchoolAdmin.Admin.ClassDetails(it)) },
                        onClassCreated = { viewModel.updateRoute(Route.SchoolAdmin.Admin.AcademicHome) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainContentArea(
    currentRoute: Route,
    onAcademicOpen: () -> Unit,
    onAcademicBack: () -> Unit,
    onAddClass: () -> Unit,
    onSelectClass: (Int) -> Unit,
    onClassCreated: () -> Unit
) {
    when (currentRoute) {
        is Route.SchoolAdmin.Admin.Home -> SchoolDashboardScreen(
            onAcademicClick = onAcademicOpen
        )
        is Route.SchoolAdmin.Admin.AcademicHome -> HomeScreen(
            onBack = onAcademicBack,
            onAddClass = onAddClass,
            onSelectClass = onSelectClass
        )
        is Route.SchoolAdmin.Admin.AcademicAdd -> AddClassScreen(
            onBack = onAcademicBack,
            onClassCreated = onClassCreated
        )
        is Route.SchoolAdmin.Admin.ClassDetails -> ClassDetailsScreen(
            classId = currentRoute.classId,
            onBack = onAcademicBack
        )
        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Module en cours de développement")
            }
        }
    }
}

@Composable
fun Sidebar(
    navItems: List<SidebarItem>,
    currentRoute: Route,
    onRouteSelected: (Route) -> Unit,
    username: String,
    role: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(AppColors.primary, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.curriculum,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Kelasi Admin",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            navItems.forEach { item ->
                SidebarNavLink(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = { onRouteSelected(item.route) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Profile Section
        HorizontalDivider(Modifier, thickness = 1.dp, color = AppColors.surfaceContainerLow)
        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AppColors.surfaceContainerLow, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    AppIcons.person,
                    contentDescription = null,
                    tint = AppColors.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = username,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = role.uppercase().replace("_", " "),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors. textSecondary,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}



data class SidebarItem(val label: String, val icon: ImageVector, val route: Route)

@Composable
fun SidebarNavLink(
    item: SidebarItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) AppColors.primary else Color.Transparent
    val contentColor = if (isSelected) Color.White else AppColors.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.label,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

