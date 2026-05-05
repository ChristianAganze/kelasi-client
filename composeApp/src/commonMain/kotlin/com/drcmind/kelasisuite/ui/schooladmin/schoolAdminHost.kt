package com.drcmind.kelasisuite.ui.schooladmin


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppColors

@Composable
fun SchoolAdminAppScreen() {
    var currentRoute by remember { mutableStateOf<Route>(Route.SystemAdmin.Dashboard) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar (Hidden on small screens in a real app, but here we'll keep it simple as per design)
        Sidebar(
            currentRoute = currentRoute,
            onRouteSelected = { currentRoute = it },
            modifier = Modifier.width(260.dp).fillMaxHeight()
        )

        // Main Content
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            when (currentRoute) {
                is Route.SystemAdmin.Dashboard -> {
                    SchoolDashboardScreen()
                }
                else -> {
                    // Placeholder for other routes
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Module en cours de développement")
                    }
                }
            }
        }
    }
}

@Composable
fun Sidebar(
    currentRoute: Route,
    onRouteSelected: (Route) -> Unit,
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
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Kelasi School Admin",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = (-0.5).sp
            )
        }

        // Nav Items
        val navItems = listOf(
            SidebarItem("Dashboard", Icons.Default.Dashboard, Route.SystemAdmin.Dashboard),
            SidebarItem("Curriculum", Icons.Default.MenuBook, Route.SystemAdmin.Curriculum),
            SidebarItem("Enrollment", Icons.Default.PersonAdd, Route.SystemAdmin.Schools), // Using Schools for Enrollment as placeholder
            SidebarItem("Financial", Icons.Default.Payments, Route.SystemAdmin.Profile), // Placeholder
            SidebarItem("Communication", Icons.Default.Chat, Route.SystemAdmin.Settings) // Placeholder
        )

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
        Divider(color = AppColors.surfaceContainerLow, thickness = 1.dp)
        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AppColors.surfaceContainerLow, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = AppColors.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Administrator",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "CORE ADMIN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.onSurfaceVariant,
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