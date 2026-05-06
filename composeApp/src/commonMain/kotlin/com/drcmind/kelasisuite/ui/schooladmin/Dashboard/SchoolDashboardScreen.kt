package com.drcmind.kelasisuite.ui.schooladmin.Dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.drcmind.kelasisuite.ui.components.AppColors
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.koinInject

@Composable
fun SchoolDashboardScreen(
    viewModel: SchoolDashboardViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.surfaceBackground)
            .padding(horizontal = 48.dp, vertical = 64.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        HeaderSection(state.username)

        Spacer(modifier = Modifier.height(64.dp))

        // Bento Grid
        BentoGrid()

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
            color = AppColors.onSurfaceVariant,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sélectionnez un module pour commencer la gestion de votre établissement.",
            fontSize = 16.sp,
            color = AppColors.onSurfaceVariant
        )
    }
}

@Composable
fun BentoGrid() {
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
                isDark = false
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

@Composable
fun BentoCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    val backgroundColor = if (isDark) AppColors.primary else Color.White
    val contentColor = if (isDark) Color.White else AppColors.onSurfaceVariant
    val secondaryContentColor =
        if (isDark) Color.White.copy(alpha = 0.7f) else AppColors.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isDark) Color.Transparent else Color(0xFFE9EDFF),
                shape = RoundedCornerShape(32.dp)
            )
            .clickable { /* Navigate */ }
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
                            color = if (isDark) Color.White.copy(alpha = 0.1f) else AppColors.surfaceContainerLow,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Icon(
                    imageVector = AppIcons.arrowFWD,
                    contentDescription = null,
                    tint = if (isDark) Color.White.copy(alpha = 0.4f) else AppColors.outlineVariant,
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
        HorizontalDivider(Modifier, thickness = 1.dp, color = Color(0xFFE9EDFF))
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
                        color = AppColors.outlineVariant,
                        letterSpacing = 1.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(6.dp)
                                .background(Color(0xFF10B981), RoundedCornerShape(3.dp))
                        )
                        Text(text = status, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Column {
                    Text(
                        text = "DERNIÈRE CONNEXION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.outlineVariant,
                        letterSpacing = 1.sp
                    )
                    Text(text = lastConnection, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Text(
                text = "© 2026 KELASI SUITE. DrcMind.",
                fontSize = 10.sp,
                color = AppColors.outlineVariant,
                letterSpacing = 0.5.sp
            )
        }
    }
}
