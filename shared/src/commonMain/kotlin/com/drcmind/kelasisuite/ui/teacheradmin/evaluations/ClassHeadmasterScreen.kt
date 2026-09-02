package com.drcmind.kelasisuite.ui.teacheradmin.evaluations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import kotlinx.coroutines.launch

data class HeadmasterStudentRow(
    val id: Long,
    val fullName: String,
    val gender: String,
    val averageScore: Double,
    val percentage: Int,
    val conduct: String,
    val bulletinComment: String
)

private val CONDUCT_OPTIONS = listOf(
    "Très Bonne (TB)",
    "Bonne (B)",
    "Passable (P)",
    "Médiocre (M)",
    "Mauvaise (MA)"
)

private val COMMENT_SUGGESTIONS = listOf(
    "Excellent trimestre ! Travail rigoureux, régulier et attitude exemplaire.",
    "Élève intelligent et appliqué. Peut encore dynamiser sa participation orale.",
    "Résultats satisfaisants. Doit redoubler d'efforts dans les matières scientifiques.",
    "Manque d'assiduité et bavardages fréquents. Un sursaut est attendu au prochain trimestre.",
    "Travail sérieux et assidu, continuez sur cette lancée pour maintenir votre rang."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassHeadmasterScreen(
    modifier: Modifier = Modifier
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpandedOrMedium = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

    var studentsList by remember {
        mutableStateOf(
            listOf(
                HeadmasterStudentRow(
                    id = 1,
                    fullName = "KABEYA MUKENDI David",
                    gender = "M",
                    averageScore = 16.4,
                    percentage = 82,
                    conduct = "Très Bonne (TB)",
                    bulletinComment = "Élève intelligent et appliqué. Peut encore dynamiser sa participation orale."
                ),
                HeadmasterStudentRow(
                    id = 2,
                    fullName = "TSHILANDA KASONGO Sarah",
                    gender = "F",
                    averageScore = 17.8,
                    percentage = 89,
                    conduct = "Très Bonne (TB)",
                    bulletinComment = "Excellent trimestre ! Travail rigoureux, régulier et attitude exemplaire."
                ),
                HeadmasterStudentRow(
                    id = 3,
                    fullName = "MBUYI ILUNGA Grâce",
                    gender = "F",
                    averageScore = 13.5,
                    percentage = 67,
                    conduct = "Bonne (B)",
                    bulletinComment = "Résultats satisfaisants. Doit redoubler d'efforts dans les matières scientifiques."
                ),
                HeadmasterStudentRow(
                    id = 4,
                    fullName = "LUMUMBA LUKUSA Patrick",
                    gender = "M",
                    averageScore = 9.8,
                    percentage = 49,
                    conduct = "Passable (P)",
                    bulletinComment = "Manque d'assiduité et bavardages fréquents. Un sursaut est attendu au prochain trimestre."
                ),
                HeadmasterStudentRow(
                    id = 5,
                    fullName = "KANYINDA KALONJI Daniel",
                    gender = "M",
                    averageScore = 14.9,
                    percentage = 74,
                    conduct = "Bonne (B)",
                    bulletinComment = "Travail sérieux et assidu, continuez sur cette lancée pour maintenir votre rang."
                )
            )
        )
    }

    var selectedStudentId by rememberSaveable { mutableStateOf<Long?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredStudents = remember(studentsList, searchQuery) {
        if (searchQuery.isBlank()) studentsList
        else studentsList.filter {
            it.fullName.contains(searchQuery, ignoreCase = true) ||
                    it.conduct.contains(searchQuery, ignoreCase = true)
        }
    }

    val selectedStudent = remember(studentsList, selectedStudentId) {
        studentsList.find { it.id == selectedStudentId }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            if (isExpandedOrMedium) {
                // Adaptive Dual-Pane (List + Detail) for Tablets / Desktop / Web
                Row(modifier = Modifier.fillMaxSize()) {
                    // LEFT PANE: Master List
                    Surface(
                        modifier = Modifier
                            .width(420.dp)
                            .fillMaxHeight(),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp
                    ) {
                        StudentListPane(
                            students = filteredStudents,
                            allStudents = studentsList,
                            selectedStudentId = selectedStudentId,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            onSelectStudent = { selectedStudentId = it.id }
                        )
                    }

                    VerticalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    // RIGHT PANE: Detail View or Empty State
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        if (selectedStudent != null) {
                            StudentDetailPane(
                                student = selectedStudent,
                                onSave = { updatedConduct, updatedComment ->
                                    studentsList = studentsList.map {
                                        if (it.id == selectedStudent.id) {
                                            it.copy(conduct = updatedConduct, bulletinComment = updatedComment)
                                        } else it
                                    }
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Appréciations de ${selectedStudent.fullName} enregistrées avec succès.")
                                    }
                                }
                            )
                        } else {
                            EmptyDetailSelectionPlaceholder()
                        }
                    }
                }
            } else {
                // Single Pane Mode for Compact / Mobile Screens
                if (selectedStudent != null) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(
                            title = { Text(selectedStudent.fullName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            navigationIcon = {
                                IconButton(onClick = { selectedStudentId = null }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour à la liste")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        StudentDetailPane(
                            student = selectedStudent,
                            onSave = { updatedConduct, updatedComment ->
                                studentsList = studentsList.map {
                                    if (it.id == selectedStudent.id) {
                                        it.copy(conduct = updatedConduct, bulletinComment = updatedComment)
                                    } else it
                                }
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Modifications enregistrées.")
                                }
                                selectedStudentId = null
                            }
                        )
                    }
                } else {
                    StudentListPane(
                        students = filteredStudents,
                        allStudents = studentsList,
                        selectedStudentId = null,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onSelectStudent = { selectedStudentId = it.id }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentListPane(
    students: List<HeadmasterStudentRow>,
    allStudents: List<HeadmasterStudentRow>,
    selectedStudentId: Long?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectStudent: (HeadmasterStudentRow) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = allStudents.size
    val girlsCount = allStudents.count { it.gender.equals("F", ignoreCase = true) }
    val boysCount = allStudents.count { it.gender.equals("M", ignoreCase = true) }
    val averageScore = if (allStudents.isNotEmpty()) allStudents.map { it.percentage }.average() else 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Class Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Espace Titulaire",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "4e Scientifique A",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$totalCount Élèves ($girlsCount F • $boysCount G)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Moyenne: ${averageScore.toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Rechercher un élève...") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Effacer", modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Élèves de la classe (${students.size})",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline
        )

        // Student List
        if (students.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aucun élève trouvé",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(students, key = { it.id }) { student ->
                    val isSelected = student.id == selectedStudentId
                    StudentMasterCard(
                        student = student,
                        isSelected = isSelected,
                        onClick = { onSelectStudent(student) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentMasterCard(
    student: HeadmasterStudentRow,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isPass = student.percentage >= 50
    val cardBg = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(borderColor)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gender/Initial Avatar
            Surface(
                shape = CircleShape,
                color = if (student.gender.equals("F", ignoreCase = true)) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = student.gender,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (student.gender.equals("F", ignoreCase = true)) {
                            MaterialTheme.colorScheme.onTertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.fullName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isPass) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${student.percentage}% (${student.averageScore}/20)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isPass) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "• ${student.conduct.takeWhile { it != '(' }.trim()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyDetailSelectionPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.widthIn(max = 440.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.AssignmentInd,
                            contentDescription = "Sélectionner un élève",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Text(
                    text = "Veuillez sélectionner un élève",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Cliquez sur un élève dans la liste de gauche pour consulter sa fiche détaillée, attribuer sa mention de conduite et rédiger l'appréciation officielle du bulletin.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudentDetailPane(
    student: HeadmasterStudentRow,
    onSave: (conduct: String, comment: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editedConduct by remember(student.id) { mutableStateOf(student.conduct) }
    var editedComment by remember(student.id) { mutableStateOf(student.bulletinComment) }

    val hasChanges = remember(student, editedConduct, editedComment) {
        editedConduct != student.conduct || editedComment != student.bulletinComment
    }

    val isPass = student.percentage >= 50
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Student Header Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (student.gender.equals("F", ignoreCase = true)) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        },
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = student.fullName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (student.gender.equals("F", ignoreCase = true)) {
                                    MaterialTheme.colorScheme.onTertiaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = student.fullName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Genre : ${if (student.gender == "F") "Féminin" else "Masculin"} • Classe : 4e Scientifique A",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Academic Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPass) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${student.percentage}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isPass) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "${student.averageScore}/20",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isPass) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // Section 1: Note de Conduite
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Gavel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "1. Note et Mention de Conduite",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Sélectionnez l'appréciation officielle du comportement de l'élève :",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CONDUCT_OPTIONS.forEach { option ->
                        val isOptionSelected = editedConduct == option
                        FilterChip(
                            selected = isOptionSelected,
                            onClick = { editedConduct = option },
                            label = {
                                Text(
                                    text = option,
                                    fontWeight = if (isOptionSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = if (isOptionSelected) {
                                {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        // Section 2: Commentaire officiel du Bulletin
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.EditNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "2. Avis & Appréciation du Titulaire",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Ce commentaire sera directement imprimé sur le bulletin scolaire officiel de l'élève.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = editedComment,
                    onValueChange = { editedComment = it },
                    placeholder = { Text("Rédigez l'avis global sur le travail et l'attitude de l'élève...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Text(
                    text = "Suggestions rapides :",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    COMMENT_SUGGESTIONS.forEach { suggestion ->
                        SuggestionChip(
                            onClick = { editedComment = suggestion },
                            label = {
                                Text(
                                    text = suggestion,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            icon = {
                                Icon(
                                    Icons.Outlined.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }
        }

        // Action Buttons Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasChanges) {
                TextButton(
                    onClick = {
                        editedConduct = student.conduct
                        editedComment = student.bulletinComment
                    }
                ) {
                    Text("Réinitialiser")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Button(
                onClick = { onSave(editedConduct, editedComment) },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Enregistrer pour le bulletin",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

