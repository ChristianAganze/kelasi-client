package com.drcmind.kelasisuite.ui.teacheradmin.classlog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class HistoricalClassLog(
    val date: String,
    val className: String,
    val subject: String,
    val timeSlot: String,
    val lessonTopic: String,
    val attendanceSummary: String,
    val status: String,
    val signedByDirection: Boolean,
    val directionFeedback: String? = null
)

@Composable
fun ClassLogHistoryScreen(
    modifier: Modifier = Modifier
) {
    val history = listOf(
        HistoricalClassLog(
            date = "24 Août 2026",
            className = "4e Scientifique A",
            subject = "Mathématiques",
            timeSlot = "08:00 - 09:40",
            lessonTopic = "Équations bicarrées et changement de variable",
            attendanceSummary = "38/40 élèves présents (2 absents justifiés)",
            status = "Signé",
            signedByDirection = true,
            directionFeedback = "Très bon déroulement, le timing a été respecté."
        ),
        HistoricalClassLog(
            date = "22 Août 2026",
            className = "4e Scientifique A",
            subject = "Physique",
            timeSlot = "10:00 - 11:40",
            lessonTopic = "Cinématique : Mouvement rectiligne uniformément varié",
            attendanceSummary = "40/40 élèves présents",
            status = "Signé",
            signedByDirection = true,
            directionFeedback = "Fiche conforme au programme national."
        ),
        HistoricalClassLog(
            date = "21 Août 2026",
            className = "3e Scientifique B",
            subject = "Mathématiques",
            timeSlot = "11:40 - 12:30",
            lessonTopic = "Factorisation par identification des coefficients",
            attendanceSummary = "35/36 élèves présents",
            status = "En attente de signature",
            signedByDirection = false
        ),
        HistoricalClassLog(
            date = "19 Août 2026",
            className = "4e Scientifique A",
            subject = "Mathématiques",
            timeSlot = "08:00 - 09:40",
            lessonTopic = "Formes canoniques et factorisation",
            attendanceSummary = "39/40 élèves présents",
            status = "Signé",
            signedByDirection = true
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Registre Historique des Journaux de Classe",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Visualisez les séances passées et les visas/signatures apposés par la Direction ou les Inspecteurs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(history) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = MaterialTheme.shapes.extraSmall,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = log.date,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = log.timeSlot,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = if (log.signedByDirection) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        if (log.signedByDirection) Icons.Default.Verified else Icons.Default.Pending,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (log.signedByDirection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = log.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (log.signedByDirection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${log.subject} • ${log.className}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Matière vue : ${log.lessonTopic}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Assiduité : ${log.attendanceSummary}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )

                        if (log.directionFeedback != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.RateReview, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Visa Direction : « ${log.directionFeedback} »",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
