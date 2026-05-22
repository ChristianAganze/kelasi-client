package com.drcmind.kelasisuite.ui.schooladmin.academics.course_affectation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.domain.dto.StudentDTO
import com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure.GlobalEnrollmentDialog
import com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure.SchoolStructureViewModel
import com.drcmind.kelasisuite.ui.schooladmin.students.StudentStatus
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseAffectation(
    viewModel: SchoolStructureViewModel = koinViewModel()
) {
    var showEnrollDialog by remember { mutableStateOf(false) }
    val enrolledStudents by viewModel.enrolledStudents.collectAsState()
    val classes by viewModel.classes.collectAsState()
    val isLoading by viewModel.isLoadingEnrolledStudents.collectAsState()

    var studentSearchQuery by remember { mutableStateOf("") }
    var classSearchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadEnrolledStudents()
    }

    Scaffold(
        containerColor = Color.Transparent, topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Affectation des élèves",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Consultez les élèves inscrits pour l'année académique active.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ), actions = {
                    Button(
                        onClick = { showEnrollDialog = true }, enabled = classes.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enroller un élève")
                    }
                })
        }) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding).padding(all = 16.dp)
        ) {


            // Main Content: Two-Column Layout
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Column(
                    Modifier.clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.surface).weight(7f).fillMaxHeight()
                        .border(

                            color = MaterialTheme.colorScheme.outlineVariant,
                            width = 1.dp,
                            shape = MaterialTheme.shapes.extraLarge,
                        ).padding(16.dp)
                ) {
                    // Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.3f)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Élèves inscrits", fontWeight = FontWeight.Bold)
                            Surface(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                                shape = CircleShape
                            ) {
                                Text(
                                    "${enrolledStudents.size} TOTAL",
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp, vertical = 2.dp
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        OutlinedTextField(
                            value = studentSearchQuery,
                            onValueChange = { studentSearchQuery = it },
                            placeholder = { Text("Filtrer nom ou ID...", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search, null, modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.width(200.dp).height(48.dp),
                            shape = MaterialTheme.shapes.large,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                    if (isLoading) {
                        Box(
                            Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    } else {
                        LazyColumn {
                            items(enrolledStudents.filter {
                                it.fullName.contains(
                                    studentSearchQuery, true
                                )
                            }) { student ->
                                StudentAffectationRow(student)
                            }
                        }
                    }
                }

                // Right Column: Class Selection
                Column(
                    modifier = Modifier.weight(5f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Search Classes Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.GridView,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "Sélectionner la classe cible",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            TextField(
                                value = classSearchQuery,
                                onValueChange = { classSearchQuery = it },
                                placeholder = {
                                    Text(
                                        "Rechercher niveau ou section...",
                                        color = Color.White.copy(alpha = 0.4f)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                )
                            )
                        }
                    }

                    // Classes Grid/List
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(classes.filter {
                            it.name.contains(
                                classSearchQuery, true
                            )
                        }) { schoolClass ->
                            ClassTargetCard(schoolClass)
                        }
                    }
                }
            }
        }
    }

    if (showEnrollDialog) {
        GlobalEnrollmentDialog(
            viewModel = viewModel,
            onDismiss = { showEnrollDialog = false },
            classId = classes.firstOrNull()?.id
        )
    }
}

@Composable
fun StudentAffectationRow(student: StudentDTO) {
    var checked by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large)
            .clickable { checked = !checked }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = { checked = it })
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(student.fullName.take(1), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                student.fullName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "ID: ${student.studentIdNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        StatusBadge(student.status)
        Icon(Icons.Rounded.ChevronRight, null, tint = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun ClassTargetCard(schoolClass: SchoolClassDTO) {
    val progress = 0.8f // Simulé pour le design
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Text(schoolClass.name.take(3), fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(schoolClass.name, fontWeight = FontWeight.Bold)
                    Text(
                        "Titulaire: Non assigné",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "CAPACITÉ",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    "${(progress * schoolClass.capacity!!).toInt()} / ${schoolClass.capacity}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
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
        color = color.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}