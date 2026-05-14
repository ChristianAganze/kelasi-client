package com.drcmind.kelasisuite.ui.schooladmin.students

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.drcmind.kelasisuite.domain.dto.StudentDTO
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    studentId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: StudentDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadStudent(studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profil de l'élève",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                    )
                }, navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    state.error!!,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            } else if (state.student != null) {
                val student = state.student!!

                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                        .padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. SECTION HEADER
                    HeaderSection(student, onEdit)

                    // 2. BENTO GRID (Informations Personnelles & Inscription)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        BentoCard(
                            title = "Informations Personnelles",
                            icon = AppIcons.person,
                            modifier = Modifier.weight(1.2f)
                        ) {
                            FlowRow(
                                maxItemsInEachRow = 2,
                                horizontalArrangement = Arrangement.spacedBy(32.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                InfoField("Nom Complet", student.fullName)
                                InfoField(
                                    "Date de Naissance", student.dateOfBirth.toString()
                                ) // Simulé
                                InfoField("Religion", student.religion ?: "N/A") // Simulé
                            }
                            Spacer(Modifier.height(16.dp))
                            InfoField("Adresse", student.address ?: "N/A")
                        }

                        BentoCard(
                            title = "Inscription Actuelle",
                            icon = AppIcons.checkMark,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.weight(0.8f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = MaterialTheme.shapes.large,
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(AppIcons.school, null, modifier = Modifier.padding(12.dp))
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(
                                        student.currentEnrollment?.className ?: "Non assigné",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black
                                    )
                                    when (student.currentEnrollment != null) {
                                        true -> Text(
                                            student.currentEnrollment.gradeLevel,
                                            style = MaterialTheme.typography.labelSmall
                                        )

                                        else -> Text(
                                            "--", style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. SECTION PARCOURS & PERFORMANCE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        BentoCard(
                            title = "Parcours",
                            icon = AppIcons.educHistory,
                            modifier = Modifier.weight(1f)
                        ) {
                            InfoField("École de provenance", student.previousSchool)
                            Spacer(Modifier.height(8.dp))
                            InfoField("Numéro Sernie", student.sernieNumber ?: "N/A")
                        }

                        // Carte Noire Performance
                        BentoCard(
                            title = "Performance",
                            icon = AppIcons.trending,
                            containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(2f)
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "78.5",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Black,
                                )
                                Text(
                                    "%",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                )
                            }
                            LinearProgressIndicator(
                                progress = { 0.78f },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                color = MaterialTheme.colorScheme.surface,
                                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(student: StudentDTO, onEdit: (Long) -> Unit) {
    Surface(
        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(32.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Placeholder
            Surface(
                modifier = Modifier.size(120.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            ) {
                Icon(
                    AppIcons.person,
                    null,
                    tint = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(6.dp).background(Color.Green, CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            student.status.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    student.fullName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    "ID: ${student.studentIdNumber}",
                    color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onEdit(student.id) }, colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White, contentColor = Color.Black
                        ), shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(AppIcons.edit, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Modifier", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (containerColor == MaterialTheme.colorScheme.surface) MaterialTheme.colorScheme.outlineVariant else Color.White.copy(
                        alpha = 0.5f
                    ),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun InfoField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}