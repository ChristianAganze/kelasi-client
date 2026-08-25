package com.drcmind.kelasisuite.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String = "Chargement des données en cours..."
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinner_angle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 4.dp,
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                    )
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .rotate(angle),
                        strokeWidth = 3.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Veuillez patienter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun friendlyErrorMessage(technical: String?): String {
    if (technical.isNullOrBlank()) return "Une erreur inattendue est survenue. Veuillez réessayer."
    val lower = technical.lowercase()
    return when {
        lower.contains("connect") || lower.contains("refused") ||
            lower.contains("timeout") || lower.contains("timed out") ||
            lower.contains("socket") || lower.contains("unknownhost") ||
            lower.contains("no route") || lower.contains("unable") ->
            "Impossible d'établir la connexion avec le serveur. Vérifiez votre accès internet et réessayez."

        lower.contains("401") || lower.contains("unauthorized") ->
            "Votre session a expiré pour des raisons de sécurité. Veuillez vous reconnecter."

        lower.contains("403") || lower.contains("forbidden") ->
            "Accès non autorisé. Vous ne disposez pas des privilèges nécessaires pour consulter cette ressource."

        lower.contains("404") || lower.contains("not found") ->
            "La ressource demandée est introuvable ou a été déplacée."

        lower.contains("408") || lower.contains("request timeout") ->
            "Le délai d'attente a été dépassé. Le serveur met du temps à répondre."

        lower.contains("502") || lower.contains("bad gateway") ||
            lower.contains("503") || lower.contains("service unavailable") ||
            lower.contains("504") || lower.contains("gateway timeout") ->
            "Le service Kelasi est momentanément indisponible pour maintenance. Veuillez réessayer dans un instant."

        lower.contains("500") || lower.contains("internal server") ->
            "Une anomalie interne est survenue sur le serveur Kelasi. Nos équipes sont informées."

        lower.contains("serialization") || lower.contains("failed to parse") ||
            lower.contains("required for type") ->
            "Erreur de traitement des données transmises par le serveur."

        else -> technical
    }
}

@Composable
fun ErrorStateCard(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.ErrorOutline
) {
    val isNetworkError = message?.lowercase()?.let {
        it.contains("connect") || it.contains("timeout") || it.contains("unknownhost") || it.contains("socket")
    } == true

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.error.copy(alpha = 0.4f),
                    MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Badge
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isNetworkError) Icons.Outlined.WifiOff else icon,
                        contentDescription = "Erreur",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Text info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isNetworkError) "Problème de connexion réseau" else "Échec de l'opération",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = friendlyErrorMessage(message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // Retry action
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Réessayer",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.SearchOff,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            if (actionLabel != null && onAction != null) {
                OutlinedButton(
                    onClick = onAction,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(actionLabel)
                }
            }
        }
    }
}

