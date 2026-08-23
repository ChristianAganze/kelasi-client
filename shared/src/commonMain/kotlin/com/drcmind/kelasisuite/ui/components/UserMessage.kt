package com.drcmind.kelasisuite.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

fun friendlyErrorMessage(technical: String?): String {
    if (technical.isNullOrBlank()) return "Une erreur est survenue. Veuillez réessayer."
    val lower = technical.lowercase()
    return when {
        lower.contains("connect") || lower.contains("refused") ||
            lower.contains("timeout") || lower.contains("timed out") ||
            lower.contains("socket") || lower.contains("unknownhost") ||
            lower.contains("no route") || lower.contains("unable") ->
            "Impossible de contacter le serveur. Vérifiez votre connexion internet et réessayez."

        lower.contains("401") || lower.contains("unauthorized") ->
            "Votre session a expiré. Veuillez vous reconnecter."

        lower.contains("403") || lower.contains("forbidden") ->
            "Vous n'avez pas l'autorisation d'accéder à ces données."

        lower.contains("404") || lower.contains("not found") ->
            "Les données demandées sont introuvables sur le serveur. Réessayez plus tard."

        lower.contains("408") || lower.contains("request timeout") ->
            "Le serveur met trop de temps à répondre. Réessayez."

        lower.contains("502") || lower.contains("bad gateway") ||
            lower.contains("503") || lower.contains("service unavailable") ||
            lower.contains("504") || lower.contains("gateway timeout") ->
            "Le service est momentanément indisponible. Réessayez plus tard."

        lower.contains("500") || lower.contains("internal server") ->
            "Une erreur est survenue côté serveur. Réessayez plus tard."

        lower.contains("serialization") || lower.contains("failed to parse") ||
            lower.contains("required for type") ->
            "Les données reçues sont invalides. Réessayez plus tard."

        else -> technical
    }
}

@Composable
fun ErrorStateCard(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.WarningAmber
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = friendlyErrorMessage(message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRetry) {
                Text("Réessayer")
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.SearchOff
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
