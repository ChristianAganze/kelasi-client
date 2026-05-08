package com.drcmind.kelasisuite.ui.schooladmin.academicManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddClassScreen(
    viewModel: AddClassViewModel = koinViewModel(),
    classId: Long? = null,
    onBack: () -> Unit = {},
    onClassCreated: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(classId) {
        if (classId != null) {
            viewModel.loadClassDetails(classId)
        }
    }

    if (state.isSuccess) {
        LaunchedEffect(Unit) {
            onClassCreated()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(48.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Header(onBack = onBack, isEdit = classId != null)
            Spacer(modifier = Modifier.height(32.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth > 980.dp

                if (isWide) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        AcademicControls(
                            state = state,
                            onNameChange = viewModel::onNameChange,
                            onSectionChange = viewModel::onSectionChange,
                            onCapacityChange = viewModel::onCapacityChange,
                            onSaveClass = {
                                if (classId != null) viewModel.updateClass(classId)
                                else viewModel.createClass()
                            },
                            onCancel = onBack,
                            modifier = Modifier.weight(2f)
                        )

                        PreviewPanel(
                            state = state,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        AcademicControls(
                            state = state,
                            onNameChange = viewModel::onNameChange,
                            onSectionChange = viewModel::onSectionChange,
                            onCapacityChange = viewModel::onCapacityChange,
                            onSaveClass = {
                                if (classId != null) viewModel.updateClass(classId)
                                else viewModel.createClass()
                            },
                            onCancel = onBack,
                            modifier = Modifier.fillMaxWidth()
                        )

                        PreviewPanel(
                            state = state,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(onBack: () -> Unit, isEdit: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(44.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.shapes.extraLarge
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Retour",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Column {
            Text(
                text = if (isEdit) "Modifier la classe" else "Ajouter une classe",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Configurez les paramètres académiques pour la promotion.",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AcademicControls(
    state: AddClassState,
    onNameChange: (String) -> Unit,
    onSectionChange: (SchoolSectionDTO) -> Unit,
    onCapacityChange: (String) -> Unit,
    onSaveClass: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        NameBlock(state.name, onNameChange)
        SectionBlock(state.sections, state.selectedSection, onSectionChange)
        CapacityBlock(state.capacity, onCapacityChange)
        ActionsBlock(
            isLoading = state.isLoading || state.isSaving,
            onSave = onSaveClass,
            onCancel = onCancel,
            errorMessage = state.errorMessage
        )
    }
}

@Composable
private fun NameBlock(name: String, onNameChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("1. Nom de la classe")
        OutlinedTextField(
            value = name,
            textStyle = MaterialTheme.typography.titleLarge,

            onValueChange = onNameChange,

            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ex: (6ème Littéraire) A") },
            shape = MaterialTheme.shapes.extraLarge
        )
    }
}

@Composable
private fun SectionBlock(
    sections: List<SchoolSectionDTO>,
    selectedSection: SchoolSectionDTO?,
    onSelectSection: (SchoolSectionDTO) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("2. Choisir la Section")
        if (sections.isEmpty()) {
            Text("Chargement des sections...", color = MaterialTheme.colorScheme.outline)
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                sections.forEach { section ->
                    AcademicSectionCard(
                        label = section.name,
                        subtitle = "Cycle: ${section.cycle}",
                        isSelected = selectedSection?.id == section.id,
                        onClick = { onSelectSection(section) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CapacityBlock(capacity: String, onCapacityChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("3. Capacité de la Classe")
        OutlinedTextField(
            value = capacity,
            onValueChange = onCapacityChange,
            textStyle = MaterialTheme.typography.titleLarge,

            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nombre d'élèves maximum") },
            shape = MaterialTheme.shapes.extraLarge
        )
    }
}

@Composable
private fun ActionsBlock(
    isLoading: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    errorMessage: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onSave,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (isLoading) "Enregistrement..." else "Enregistrer",
                    fontWeight = FontWeight.SemiBold
                )
            }
            OutlinedButton(onClick = onCancel, enabled = !isLoading) {
                Text(text = "Annuler", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (!errorMessage.isNullOrBlank()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
    }
}

@Composable
private fun PreviewPanel(state: AddClassState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Aperçu",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = (state.selectedSection?.name + " " + state.name).ifBlank { "Nouvelle Classe" },
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = "Section",
            style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = state.selectedSection?.name ?: "Non sélectionnée",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Capacité",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
            Text(
                text = "${state.capacity} élèves",
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}


@Composable
private fun SectionTitle(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun AcademicSectionCard(
    label: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val background =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val subtitleColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
            alpha = 0.7f
        )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .clickable { onClick() }
            .padding(20.dp)
            .width(200.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = AppIcons.school,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(text = subtitle, style = MaterialTheme.typography.titleMedium, color = subtitleColor)
    }
}
