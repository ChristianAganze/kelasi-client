package com.drcmind.kelasisuite.ui.parentadmin.children

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.drcmind.kelasisuite.data.datasource.remote.dto.ChildDTO
import com.drcmind.kelasisuite.domain.model.common.ElectronicSignature
import com.drcmind.kelasisuite.domain.model.parent.AbsenceJustification
import com.drcmind.kelasisuite.ui.components.signature.ElectronicSignatureDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsenceJustificationDialog(
    child: ChildDTO,
    onDismiss: () -> Unit,
    onSubmit: (AbsenceJustification) -> Unit
) {
    var absenceDate by remember { mutableStateOf("Aujourd'hui, 24 Août 2026") }
    var selectedReason by remember { mutableStateOf("Maladie / Raison Médicale") }
    val reasons = listOf(
        "Maladie / Raison Médicale",
        "Cas de force majeure / Urgence familiale",
        "Rendez-vous administratif ou consulaire",
        "Voyage d'études / Déplacement prévu",
        "Autre motif exceptionnel"
    )
    var expandedReasonDropdown by remember { mutableStateOf(false) }
    var explanation by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("Parent Titulaire") }
    var parentPhone by remember { mutableStateOf("+243 820 000 123") }

    var parentSignature by remember { mutableStateOf<ElectronicSignature?>(null) }
    var showSignatureDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Justification d'Absence",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Élève : ${child.firstName} ${child.lastName} (${child.className})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                HorizontalDivider()

                // Date of Absence
                OutlinedTextField(
                    value = absenceDate,
                    onValueChange = { absenceDate = it },
                    label = { Text("Date de l'absence") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedReasonDropdown,
                    onExpandedChange = { expandedReasonDropdown = !expandedReasonDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedReason,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Motif de l'absence") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReasonDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedReasonDropdown,
                        onDismissRequest = { expandedReasonDropdown = false }
                    ) {
                        reasons.forEach { reason ->
                            DropdownMenuItem(
                                text = { Text(reason) },
                                onClick = {
                                    selectedReason = reason
                                    expandedReasonDropdown = false
                                }
                            )
                        }
                    }
                }

                // Explanation Note
                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("Mot d'explication / Détails") },
                    placeholder = { Text("Ex: L'élève a présenté une forte fièvre ce matin...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 4
                )

                // Parent contact info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("Nom du responsable") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = parentPhone,
                        onValueChange = { parentPhone = it },
                        label = { Text("Téléphone") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Signature section
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Signature du Parent / Tuteur", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            if (parentSignature != null) {
                                Text("Certifié : ${parentSignature!!.signatureToken}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            } else {
                                Text("Signature requise pour transmission", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                            }
                        }

                        Button(
                            onClick = { showSignatureDialog = true },
                            colors = if (parentSignature != null) ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)) else ButtonDefaults.buttonColors(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (parentSignature != null) "Re-signer" else "Signer")
                        }
                    }
                }

                // Submit Button
                Button(
                    onClick = {
                        val justification = AbsenceJustification(
                            id = "JUSTIF-${child.id}-${(1000..9999).random()}",
                            childId = child.id,
                            childName = "${child.firstName} ${child.lastName}",
                            absenceDate = absenceDate,
                            reasonCategory = selectedReason,
                            explanation = explanation.ifBlank { "Justification transmise par le responsable légal." },
                            parentName = parentName,
                            parentPhone = parentPhone,
                            submittedAt = "24/08/2026 à 08:30",
                            status = "Transmis au Préfet",
                            electronicSignature = parentSignature
                        )
                        onSubmit(justification)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = explanation.isNotBlank() || selectedReason.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Transmettre à la Direction Scolaire")
                }
            }
        }
    }

    if (showSignatureDialog) {
        ElectronicSignatureDialog(
            signerName = parentName,
            signerRole = "Parent / Responsable Légal",
            documentTitle = "Justification d'absence de ${child.firstName} ${child.lastName}",
            onDismiss = { showSignatureDialog = false },
            onConfirmSignature = { sig ->
                parentSignature = sig
                showSignatureDialog = false
            }
        )
    }
}
