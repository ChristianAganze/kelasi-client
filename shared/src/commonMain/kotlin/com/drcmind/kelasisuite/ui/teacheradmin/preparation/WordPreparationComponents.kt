package com.drcmind.kelasisuite.ui.teacheradmin.preparation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.drcmind.kelasisuite.domain.model.teacher.LessonPreparation
import com.drcmind.kelasisuite.domain.model.teacher.PreparationTemplate
import com.drcmind.kelasisuite.domain.model.teacher.standardLessonTemplates
import com.drcmind.kelasisuite.ui.components.signature.RenderElectronicSignature

@Composable
fun WordPreparationPreviewDialog(
    preparation: LessonPreparation,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar estilo Word
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2B579A) // Word Blue
                            ) {
                                Text(
                                    text = "W",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Fiche_Preparation_${preparation.header.lessonSubject.take(20)}.docx",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Aperçu Document Officiel • Format A4 / Didactique",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }
                }

                // Document Canvas (Feuille A4 style Word)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = 850.dp)
                            .shadow(8.dp, RoundedCornerShape(4.dp)),
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // En-tête officiel du document
                            Text(
                                text = "RÉPUBLIQUE DÉMOCRATIQUE DU CONGO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "MINISTÈRE DE L'ÉDUCATION NATIONALE ET NOUVELLE CITOYENNETÉ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Surface(
                                color = Color(0xFFF0F4F8),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF2B579A), RoundedCornerShape(4.dp))
                            ) {
                                Text(
                                    text = "FICHE DE PRÉPARATION PÉDAGOGIQUE",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2B579A),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Grille administrative (En-tête de la fiche)
                            DocumentSectionTitle("1. IDENTIFICATION ADMINISTRATIVE")
                            Spacer(modifier = Modifier.height(8.dp))

                            DocumentKeyValueRow("Branche / Discipline :", preparation.header.branch, "Sous-branche :", preparation.header.subBranch.ifBlank { "-" })
                            DocumentKeyValueRow("Classe :", preparation.header.className, "Date :", preparation.dateCreated.take(10))
                            DocumentKeyValueRow("Sujet de révision :", preparation.header.revisionSubject.ifBlank { "Néant" }, "Matériel didactique :", preparation.header.didacticMaterial.ifBlank { "Tableau & Craie" })
                            DocumentFullWidthRow("Sujet de la leçon (Titre) :", preparation.header.lessonSubject)
                            DocumentFullWidthRow("Objectif Opérationnel :", preparation.header.operationalObjective)
                            DocumentFullWidthRow("Références bibliographiques :", preparation.header.bibliography.ifBlank { "Programme National" })

                            Spacer(modifier = Modifier.height(20.dp))

                            // Tableau didactique de déroulement
                            DocumentSectionTitle("2. DÉROULEMENT DIDACTIQUE DE LA LEÇON")
                            Spacer(modifier = Modifier.height(8.dp))

                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF2B579A))
                                    .border(1.dp, Color.Black)
                            ) {
                                TableCell("Étapes didactiques", weight = 0.25f, isHeader = true)
                                TableCell("Durée", weight = 0.15f, isHeader = true)
                                TableCell("Méthodes & Procédés", weight = 0.25f, isHeader = true)
                                TableCell("Matières & Activités", weight = 0.35f, isHeader = true)
                            }

                            // Table Rows
                            DidacticTableRow(
                                stepName = "I. INTRODUCTION",
                                duration = preparation.steps.introduction.duration.ifBlank { "5 min" },
                                method = preparation.steps.introduction.method.ifBlank { "Interrogative" },
                                content = preparation.steps.introduction.content.ifBlank { "Rappel et motivation" }
                            )

                            DidacticTableRow(
                                stepName = "II. DÉVELOPPEMENT",
                                duration = preparation.steps.development.duration.ifBlank { "30 min" },
                                method = preparation.steps.development.method.ifBlank { "Expositive / Active" },
                                content = preparation.steps.development.content.ifBlank { "Explication et analyse détaillée" }
                            )

                            DidacticTableRow(
                                stepName = "III. SYNTHÈSE",
                                duration = preparation.steps.synthesis.duration.ifBlank { "10 min" },
                                method = preparation.steps.synthesis.method.ifBlank { "Résumé / Tableau" },
                                content = preparation.steps.synthesis.content.ifBlank { "Fixation des concepts clés" }
                            )

                            DidacticTableRow(
                                stepName = "IV. APPLICATION",
                                duration = preparation.steps.application.duration.ifBlank { "5 min" },
                                method = preparation.steps.application.method.ifBlank { "Exercices écrits" },
                                content = preparation.steps.application.content.ifBlank { "Tâches individuelles d'évaluation" }
                            )

                            Spacer(modifier = Modifier.height(30.dp))

                            // Signatures
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("L'Enseignant titulaire", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (preparation.teacherSignature != null) {
                                        RenderElectronicSignature(preparation.teacherSignature)
                                    } else {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text("(Signature manuelle)", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Le Préfet des Études / Conseiller", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (preparation.validatorSignature != null) {
                                        RenderElectronicSignature(preparation.validatorSignature)
                                    } else {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text("(Visa didactique)", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateSelectorDialog(
    onDismiss: () -> Unit,
    onSelectTemplate: (PreparationTemplate) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Article, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Modèles de Leçon Didactique (Word)", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(standardLessonTemplates) { tpl ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        onClick = { onSelectTemplate(tpl) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(tpl.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = tpl.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(tpl.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Exemple : « ${tpl.lessonSubject} »", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
private fun DocumentSectionTitle(title: String) {
    Surface(
        color = Color(0xFFE8EEF5),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B579A),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DocumentKeyValueRow(k1: String, v1: String, k2: String, v2: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Text(k1, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(v1, fontSize = 11.sp, color = Color.DarkGray)
        }
        Row(modifier = Modifier.weight(1f)) {
            Text(k2, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(v2, fontSize = 11.sp, color = Color.DarkGray)
        }
    }
}

@Composable
private fun DocumentFullWidthRow(key: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(key, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(
            text = value.ifBlank { "-" },
            fontSize = 11.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
        )
    }
}

@Composable
private fun DidacticTableRow(
    stepName: String,
    duration: String,
    method: String,
    content: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray)
    ) {
        TableCell(stepName, weight = 0.25f, isBold = true)
        TableCell(duration, weight = 0.15f)
        TableCell(method, weight = 0.25f)
        TableCell(content, weight = 0.35f)
    }
}

@Composable
private fun RowScope.TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false,
    isBold: Boolean = false
) {
    Box(
        modifier = Modifier
            .weight(weight)
            .padding(6.dp)
    ) {
        Text(
            text = text,
            fontSize = if (isHeader) 11.sp else 10.sp,
            fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isHeader) Color.White else Color.Black
        )
    }
}
