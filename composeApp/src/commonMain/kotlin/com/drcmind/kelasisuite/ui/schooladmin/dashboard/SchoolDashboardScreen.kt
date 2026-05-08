package com.drcmind.kelasisuite.ui.schooladmin.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.koinInject

@Composable
fun SchoolDashboardScreen(
    viewModel: SchoolDashboardViewModel = koinInject(),
    onAcademicClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(horizontal = 48.dp, vertical = 64.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        HeaderSection(state.username)

        Spacer(modifier = Modifier.height(64.dp))

        // Bento Grid
        BentoGrid(onAcademicClick = onAcademicClick)

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(64.dp))

        // Footer
        FooterSection(state.systemStatus, state.lastConnection)
    }
}

@Composable
fun HeaderSection(username: String) {
    Column {
        Text(
            text = "Bienvenue, $username",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sélectionnez un module pour commencer la gestion de votre établissement.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BentoGrid(
    onAcademicClick: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(
            modifier = Modifier.height(320.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Gestion Académique (Large)
            BentoCard(
                title = "Gestion Académique",
                description = "Focus on student data, classroom assignments, and the overarching school organizational structure.",
                icon = AppIcons.school,
                modifier = Modifier.weight(2f),
                isDark = false,
                onClick = onAcademicClick
            )

            // Curriculum & Notes (Tall/Dark)
            BentoCard(
                title = "Curriculum & Notes",
                description = "Access the grading matrix, subject management, and student performance tracking.",
                icon = AppIcons.curriculum,
                modifier = Modifier.weight(1f),
                isDark = true
            )
        }

        Row(
            modifier = Modifier.height(240.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Inscriptions
            BentoCard(
                title = "Inscriptions",
                description = "Manage new student enrollments and digital student profiles.",
                icon = AppIcons.personAdd,
                modifier = Modifier.weight(1f),
                isDark = false
            )

            // Finances
            BentoCard(
                title = "Finances & Facturation",
                description = "Track payments, tuition fees, and institutional billing cycles.",
                icon = AppIcons.payments,
                modifier = Modifier.weight(1f),
                isDark = false
            )

            // Communication
            BentoCard(
                title = "Communication",
                description = "Real-time updates, announcements, and parent-teacher messaging.",
                icon = AppIcons.communication,
                modifier = Modifier.weight(1f),
                isDark = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BentoCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor =
        if (isDark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
    val contentColor =
        if (isDark) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val secondaryContentColor =
        if (isDark) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraExtraLarge)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isDark) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.extraExtraLarge
            )
            .clickable { onClick() }
            .padding(32.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = if (isDark) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDark) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Icon(
                    imageVector = AppIcons.arrowFWD,
                    contentDescription = null,
                    tint = if (isDark) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = secondaryContentColor,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun FooterSection(status: String, lastConnection: String) {
    Column {
        HorizontalDivider(
            Modifier,
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                Column {
                    Text(
                        text = "STATUS SYSTÈME",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 1.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Text(
                            text = status,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Column {
                    Text(
                        text = "DERNIÈRE CONNEXION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = lastConnection,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = "© 2026 KELASI SUITE. DrcMind.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 0.5.sp
            )
        }
    }
}