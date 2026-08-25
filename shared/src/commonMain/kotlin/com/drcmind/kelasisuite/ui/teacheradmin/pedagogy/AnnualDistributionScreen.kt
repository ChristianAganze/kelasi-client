package com.drcmind.kelasisuite.ui.teacheradmin.pedagogy

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

data class ChapterProgress(
    val weekNumber: Int,
    val period: String,
    val chapterTitle: String,
    val subTopics: List<String>,
    val hoursAllocated: Int,
    val isCompleted: Boolean = false,
    val isCurrent: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnualDistributionScreen(
    modifier: Modifier = Modifier
) {
    var selectedCourse by remember { mutableStateOf("Mathématiques - 4e Secondaire Scientifique") }
    val courseOptions = listOf(
        "Mathématiques - 4e Secondaire Scientifique",
        "Physique - 4e Secondaire Scientifique",
        "Chimie - 3e Secondaire Scientifique"
    )
    var courseMenuExpanded by remember { mutableStateOf(false) }

    val curriculumPlan = listOf(
        ChapterProgress(1, "1ère Période", "Chapitre 1 : Rappels sur les équations et inéquations du 1er degré", listOf("Formes canoniques", "Résolution graphique", "Applications économiques"), 6, isCompleted = true),
        ChapterProgress(2, "1ère Période", "Chapitre 2 : Systèmes linéaires à deux et trois inconnues", listOf("Méthode de substitution", "Méthode de Cramer (déterminants)", "Résolution matricielle"), 8, isCompleted = true),
        ChapterProgress(3, "1ère Période", "Chapitre 3 : Polynômes et factorisation avancée", listOf("Division euclidienne", "Schéma de Horner", "Racines évidentes"), 6, isCompleted = true),
        ChapterProgress(4, "1ère Période", "Chapitre 4 : Équations du second degré et discriminant", listOf("Calcul du Delta", "Signe des racines", "Équations bicarrées"), 8, isCurrent = true),
        ChapterProgress(5, "2ème Période", "Chapitre 5 : Généralités sur les fonctions numériques", listOf("Domaine de définition", "Parité et périodicité", "Sens de variation"), 8),
        ChapterProgress(6, "2ème Période", "Chapitre 6 : Limites de fonctions et continuité", listOf("Limites usuelles", "Formes indéterminées 0/0 et inf/inf", "Continuité en un point"), 10),
        ChapterProgress(7, "2ème Période", "Chapitre 7 : Trigonométrie circulaire et formules d'addition", listOf("Cercle trigonométrique", "Formules de duplication", "Équations trigonométriques"), 8),
        ChapterProgress(8, "3ème Période", "Chapitre 8 : Géométrie vectorielle dans le plan et l'espace", listOf("Produit scalaire", "Vecteurs directeurs et normaux", "Équations de droites et plans"), 8)
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
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Répartition Annuelle des Matières",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Consultez le découpage officiel des matières par semaine et par période pour vos cours assignés.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = courseMenuExpanded,
                    onExpandedChange = { courseMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCourse,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cours sélectionné") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseMenuExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = courseMenuExpanded,
                        onDismissRequest = { courseMenuExpanded = false }
                    ) {
                        courseOptions.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                onClick = {
                                    selectedCourse = course
                                    courseMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progression du Programme (Semaine 1 à 30)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Taux d'avancement : 38%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(curriculumPlan) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            item.isCurrent -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            item.isCompleted -> MaterialTheme.colorScheme.surface
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        }
                    ),
                    border = if (item.isCurrent) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else null
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
                                    color = if (item.isCompleted) MaterialTheme.colorScheme.primary else if (item.isCurrent) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
                                ) {
                                    Text(
                                        text = "Sem. ${item.weekNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.period,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (item.isCompleted) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Vu en classe", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                            } else if (item.isCurrent) {
                                Surface(shape = MaterialTheme.shapes.extraSmall, color = MaterialTheme.colorScheme.tertiaryContainer) {
                                    Text("En cours cette semaine", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.chapterTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        item.subTopics.forEach { topic ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.ArrowRight, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Volume horaire : ${item.hoursAllocated}h",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
