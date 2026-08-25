package com.drcmind.kelasisuite.ui.teacheradmin.evaluations

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class HeadmasterStudentRow(
    val id: Long,
    val fullName: String,
    val gender: String,
    val averageScore: Double,
    val percentage: Int,
    val conduct: String,
    val bulletinComment: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassHeadmasterScreen(
    modifier: Modifier = Modifier
) {
    val conductOptions = listOf("Très Bonne (TB)", "Bonne (B)", "Passable (P)", "Médiocre (M)", "Mauvaise (MA)")
    val commentSuggestions = listOf(
        "Élève intelligent, discipliné et assidu. Peut encore améliorer sa participation en classe.",
        "Excellent trimestre ! Travail rigoureux et régulier, félicitations.",
        "Résultats satisfaisants. Doit redoubler d'efforts dans les matières scientifiques.",
        "Manque d'assiduité et bavardages fréquents. Un sursaut est attendu au prochain trimestre.",
        "Travail sérieux, continuez ainsi pour maintenir votre rang."
    )

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
                    bulletinComment = "Élève intelligent, discipliné et assidu. Peut encore améliorer sa participation en classe."
                ),
                HeadmasterStudentRow(
                    id = 2,
                    fullName = "TSHILANDA KASONGO Sarah",
                    gender = "F",
                    averageScore = 17.8,
                    percentage = 89,
                    conduct = "Très Bonne (TB)",
                    bulletinComment = "Excellent trimestre ! Travail rigoureux et régulier, félicitations."
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
                    bulletinComment = "Travail sérieux, continuez ainsi pour maintenir votre rang."
                )
            )
        )
    }

    var selectedStudentForEdit by remember { mutableStateOf<HeadmasterStudentRow?>(null) }
    var editedConduct by remember { mutableStateOf("") }
    var editedComment by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Hero card Titularisation
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Espace Professeur Titulaire",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "Classe : 4e Scientifique A",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Effectif Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text("${studentsList.size} Élèves", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Répartition F/G", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text("2 Filles • 3 Garçons", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Moyenne Générale", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text("72.2 %", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Délibération & Saisie de la Conduite / Avis du Bulletin",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Attribuez la note de conduite et rédigez le commentaire officiel qui figurera sur le bulletin scolaire.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(studentsList) { student ->
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
                                        shape = MaterialTheme.shapes.small,
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = student.gender,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = student.fullName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Moyenne tous cours : ${student.averageScore}/20 (${student.percentage}%)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (student.percentage >= 50) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        text = "Conduite : ${student.conduct}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Commentaire Titulaire sur le Bulletin :",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "« ${student.bulletinComment} »",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        selectedStudentForEdit = student
                                        editedConduct = student.conduct
                                        editedComment = student.bulletinComment
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Modifier Conduite & Commentaire")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedStudentForEdit != null) {
        val student = selectedStudentForEdit!!
        var conductDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { selectedStudentForEdit = null },
            title = { Text("Édition Titulaire : ${student.fullName}") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "1. Conduite de l'élève",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    ExposedDropdownMenuBox(
                        expanded = conductDropdownExpanded,
                        onExpandedChange = { conductDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = editedConduct,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Appréciation de conduite") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conductDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = conductDropdownExpanded,
                            onDismissRequest = { conductDropdownExpanded = false }
                        ) {
                            conductOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        editedConduct = option
                                        conductDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text(
                        text = "2. Commentaire général du Bulletin",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = editedComment,
                        onValueChange = { editedComment = it },
                        label = { Text("Avis & Recommandations") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 4
                    )

                    Text(
                        text = "Suggestions rapides :",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    commentSuggestions.take(3).forEach { suggestion ->
                        AssistChip(
                            onClick = { editedComment = suggestion },
                            label = { Text(suggestion, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        studentsList = studentsList.map {
                            if (it.id == student.id) {
                                it.copy(conduct = editedConduct, bulletinComment = editedComment)
                            } else it
                        }
                        selectedStudentForEdit = null
                    }
                ) {
                    Text("Valider pour le bulletin")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedStudentForEdit = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}
