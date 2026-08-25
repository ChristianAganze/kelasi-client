package com.drcmind.kelasisuite.ui.teacheradmin.pedagogy

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

data class CurriculumCompetency(
    val domain: String,
    val targetCompetency: String,
    val learningOutcomes: List<String>,
    val methodologicalGuidance: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NationalCurriculumScreen(
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember { mutableStateOf("Mathématiques - Secondaire Général et Humanités") }
    val subjects = listOf(
        "Mathématiques - Secondaire Général et Humanités",
        "Sciences Physiques - Secondaire Scientifique",
        "Chimie & Biologie - Humanités Scientifiques",
        "Français & Littérature - Secondaire Général",
        "Histoire & Éducation à la Citoyenneté"
    )
    var subjectMenuExpanded by remember { mutableStateOf(false) }

    val competencies = listOf(
        CurriculumCompetency(
            domain = "Algèbre & Analyse",
            targetCompetency = "Résoudre des situations-problèmes faisant appel aux équations, inéquations et systèmes de fonctions réelles.",
            learningOutcomes = listOf(
                "Modéliser une situation concrète sous forme d'équation ou de système d'inéquations.",
                "Étudier les variations et extremums de fonctions usuelles.",
                "Interpréter graphiquement les solutions d'un problème d'optimisation."
            ),
            methodologicalGuidance = "Privilégier la démarche active par résolution de situations-problèmes ancrées dans l'environnement quotidien de l'élève (gestion, commerce, géométrie)."
        ),
        CurriculumCompetency(
            domain = "Géométrie & Trigonométrie",
            targetCompetency = "Utiliser les outils vectoriels et trigonométriques pour modéliser des configurations spatiales et calculer des grandeurs.",
            learningOutcomes = listOf(
                "Calculer des produits scalaires et vectoriels dans l'espace.",
                "Déterminer les équations cartésiennes de plans et droites sécantes ou parallèles.",
                "Résoudre des triangles à l'aide des formules d'addition et de duplication trigonométrique."
            ),
            methodologicalGuidance = "Faire manipuler des représentations 3D et dessins en perspective cavalière pour consolider l'intuition spatiale."
        ),
        CurriculumCompetency(
            domain = "Statistiques & Probabilités",
            targetCompetency = "Traiter des séries statistiques bivariées et évaluer des probabilités discrètes dans des contextes aléatoires.",
            learningOutcomes = listOf(
                "Calculer la moyenne, variance, écart-type et covariance d'une série statistique double.",
                "Ajuster un nuage de points par la droite des moindres carrés.",
                "Calculer des probabilités conditionnelles avec des arbres pondérés."
            ),
            methodologicalGuidance = "Utiliser des jeux de données réels (démographie RDC, météo, santé publique) pour stimuler la réflexion critique."
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
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Programme National Officiel (Ministère de l'Éducation Nationale)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Consultez le référentiel officiel des compétences de base, les acquis d'apprentissage visés et les directives pédagogiques obligatoires.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = subjectMenuExpanded,
                    onExpandedChange = { subjectMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Discipline et Niveau") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectMenuExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = subjectMenuExpanded,
                        onDismissRequest = { subjectMenuExpanded = false }
                    ) {
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                onClick = {
                                    selectedSubject = subject
                                    subjectMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Compétences Terminales & Directives Méthodologiques",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(competencies) { comp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = comp.domain,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = comp.targetCompetency,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Acquis d'apprentissage visés :",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        comp.learningOutcomes.forEach { outcome ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = outcome,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Directives Méthodologiques de l'Inspection :",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = comp.methodologicalGuidance,
                                        style = MaterialTheme.typography.bodySmall,
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
