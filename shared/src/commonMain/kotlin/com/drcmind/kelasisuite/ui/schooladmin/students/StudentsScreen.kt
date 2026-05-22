package com.drcmind.kelasisuite.ui.schooladmin.students

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentsScreen(
    viewModel: StudentsViewModel = koinViewModel()
) {
    val uiState by viewModel.listState.collectAsState()

    val studentsBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Students.ListDetails::class,
                        Route.SchoolAdmin.Students.ListDetails.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Students.ListDetails
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = Color.Transparent,
                ),
                title = {
                    Column {
                        Text(
                            text = "Gestion des Élèves",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Gérez la base de données académique et les profils de vos élèves/étudiants.",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::loadStudents) {
                        Icon(
                            AppIcons.refresh,
                            contentDescription = "Actualiser",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(),
                        onClick = {
                            studentsBackStack.add(Route.SchoolAdmin.AddStudent())
                        }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ajouter un élève")
                        }
                    }
                }
            )
        },
        containerColor = Color.Transparent,
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(start = 16.dp)) {
            // StatsGrid(uiState)
            // Spacer(modifier = Modifier.height(32.dp))
            StudentsNavigation(
                modifier = Modifier.fillMaxSize(),
                studentsBackStack = studentsBackStack
            )
        }
    }
}

@Composable
fun StatsGrid(state: StudentUiState) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Total Élèves", state.totalStudents.toString(), "+4.2%", Modifier.weight(1f))
        StatCard("Nouveaux", state.newStudents.toString(), "2023-2024", Modifier.weight(1f))
        StatCard(
            "En Attente",
            state.pendingActionCount.toString(),
            "Documents",
            Modifier.weight(1f),
            isWarning = true
        )
        StatCard("Diplômés", state.graduateCount.toString(), "Promotion", Modifier.weight(1f))
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    trend: String,
    modifier: Modifier = Modifier,
    isWarning: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                trend,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}
