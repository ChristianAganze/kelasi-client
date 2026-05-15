package com.drcmind.kelasisuite.ui.schooladmin.students

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    viewModel: StudentsViewModel = koinViewModel(),
    onNavigateToAddStudent: () -> Unit,
    onNavigateToStudentDetail: (Long) -> Unit
) {
    val uiState by viewModel.listState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme
                        .onSurface,
                    actionIconContentColor = Color.Transparent,
                ),

                title = {
                    Column {
                        Text(
                            text = "Gestion des Élèves",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight =
                                    FontWeight.Black
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
                            onNavigateToAddStudent()
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter un élève")
                    }
                }
            )
        },
        containerColor = Color.Transparent,
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {

                item {
                    StatsGrid(uiState)
                }

                item {
                    StudentTableCard(
                        uiState.students,
                        onNavigateToStudentDetail,
                        uiState.searchQuery,
                        viewModel::onSearchQueryChange
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun StatsGrid(state: StudentUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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

@Composable
fun StudentTableCard(
    students: List<StudentItem>,
    onNavigateToStudentDetail: (Long) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {


            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp),
                placeholder = { Text("Rechercher un élève (Nom, Matricule, Classe)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },

                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
            )


            // Simulation du header de table
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "NOMS",
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    "MATRICULE",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center

                )
                Text(
                    "ADDRESSE",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center

                )
                Text(
                    "STATUT",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center

                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            when (students.isEmpty()) {
                true -> Column(
                    modifier = Modifier.fillMaxSize().padding(vertical = 40.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(AppIcons.peoples, contentDescription = null, Modifier.size(100.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("Aucun élève/étudiant trouvé")
                }

                false -> students.forEach { student ->
                    StudentRow(student) {
                        onNavigateToStudentDetail(student.id.toLong())
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }

        }
    }
}

@Composable
fun StudentRow(
    student: StudentItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.clickable { onClick() }

    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(2f)) {
                Box(
                    Modifier.size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(student.name.take(1), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        student.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        student.dateOfBirth,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Text(
                student.matricule,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center

            )
            Text(
                student.adress,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Black

                ),
                textAlign = TextAlign.Center
            )
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                StatusBadge(student.status)
            }
        }
    }
}

@Composable
fun StatusBadge(status: StudentStatus) {
    val color = when (status) {
        StudentStatus.ACTIVE -> Color(0xFF10B981)
        StudentStatus.PROBATION -> Color(0xFFF59E0B)
        StudentStatus.INACTIVE -> MaterialTheme.colorScheme.outline
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(Modifier.size(6.dp).background(color, CircleShape))
            Spacer(Modifier.width(6.dp))
            Text(status.name, style = MaterialTheme.typography.bodyLarge, color = color)
        }
    }
}
