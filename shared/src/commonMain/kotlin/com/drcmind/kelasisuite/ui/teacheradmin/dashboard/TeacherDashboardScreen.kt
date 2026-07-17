package com.drcmind.kelasisuite.ui.teacheradmin.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: TeacherDashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Bonjour, ${state.username}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Prochain cours",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else if (state.errorMessage != null) {
                        Text(text = "Erreur: ${state.errorMessage}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text(text = state.nextClass, style = MaterialTheme.typography.bodyLarge)
                        if (state.nextClassTime.isNotBlank()) {
                            Text(text = state.nextClassTime, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Tâches en attente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (state.pendingClassLogs > 0) {
                                    Badge { Text(state.pendingClassLogs.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = "Journaux", modifier = Modifier.size(32.dp))
                        }
                        Text("Journaux de classe", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                OutlinedCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (state.pendingEvaluations > 0) {
                                    Badge { Text(state.pendingEvaluations.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Numbers, contentDescription = "Evaluations", modifier = Modifier.size(32.dp))
                        }
                        Text("Cotes à saisir", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
