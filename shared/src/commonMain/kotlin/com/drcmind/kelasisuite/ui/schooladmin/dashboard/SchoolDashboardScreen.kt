package com.drcmind.kelasisuite.ui.schooladmin.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
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

    LazyVerticalGrid(
        // S'adapte automatiquement à la largeur disponible (min 300dp par colonne)
        columns = GridCells.Adaptive(minSize = 200.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Header Section (Prend toute la largeur)
        item(span = { GridItemSpan(maxLineSpan) }) {
            HeaderSection(state.username)
        }


        item(span = { GridItemSpan(if (maxLineSpan >= 2) 2 else 1) }) {
            BentoCard(
                title = "Gestion Académique",
                description = "Données élèves, affectations des classes et structure organisationnelle de l'établissement.",
                icon = AppIcons.school,
                modifier = Modifier.height(120.dp),
                isDark = false,
                onClick = onAcademicClick
            )
        }
        item {
            BentoCard(
                title = "Curriculum & Notes",
                description = "Grilles d'évaluation, gestion des matières et suivi des performances.",
                icon = AppIcons.curriculum,
                modifier = Modifier.height(140.dp),
                isDark = true
            )
        }



        item {
            BentoCard(
                title = "Inscriptions",
                description = "Gérez les nouvelles admissions et les profils numériques.",
                icon = AppIcons.personAdd,
                modifier = Modifier.height(140.dp),
                isDark = false
            )
        }

        item {
            BentoCard(
                title = "Finances",
                description = "Suivi des paiements, frais de scolarité et cycles de facturation.",
                icon = AppIcons.payments,
                modifier = Modifier.height(140.dp),
                isDark = false
            )
        }

        item {
            BentoCard(
                title = "Communication",
                description = "Mises à jour en temps réel, annonces et messagerie parents.",
                icon = AppIcons.communication,
                modifier = Modifier.height(140.dp),
                isDark = false
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Spacer(modifier = Modifier.height(32.dp))
                FooterSection(state.systemStatus, state.lastConnection)
            }
        }
    }
}

@Composable
fun HeaderSection(username: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        // Avatar circulaire avec initiale
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = username.take(1).uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = "Bonjour, $username",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Gérez votre établissement efficacement.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            .clip(MaterialTheme.shapes.extraLarge)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isDark) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Icône dans un conteneur arrondi
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isDark) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Icon(
                    imageVector = AppIcons.arrowFWD,
                    contentDescription = null,
                    tint = if (isDark) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = secondaryContentColor,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

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
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column {
                    Text(
                        text = "STATUS SYSTÈME",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 1.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Text(
                            text = status,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Column {
                    Text(
                        text = "CONNEXION",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = lastConnection,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = "© 2026 KELASI SUITE",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
