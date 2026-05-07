package com.drcmind.kelasisuite.ui.schooladmin.AcademicManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.koinInject

@Composable
fun AddClassScreen(
    viewModel: AddClassViewModel = koinInject(),
    onBack: () -> Unit = {},
    onClassCreated: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface) // Remplacé surfaceBackground
            .padding(48.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(onBack = onBack)
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
                            onSelectSection = viewModel::selectSection,
                            onSelectOption = viewModel::selectOption,
                            onSetCapacity = viewModel::setCapacity,
                            onCreateClass = {
                                viewModel.createClass { result ->
                                    if (result is CreateClassResult.Success) {
                                        onClassCreated()
                                    }
                                }
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
                            onSelectSection = viewModel::selectSection,
                            onSelectOption = viewModel::selectOption,
                            onSetCapacity = viewModel::setCapacity,
                            onCreateClass = {
                                viewModel.createClass { result ->
                                    if (result is CreateClassResult.Success) {
                                        onClassCreated()
                                    }
                                }
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
private fun Header(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Retour",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Column {
            Text(
                text = "Ajouter une classe",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Configurez les paramètres académiques pour la nouvelle promotion.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AcademicControls(
    state: AddClassState,
    onSelectSection: (AcademicSection) -> Unit,
    onSelectOption: (AcademicOption) -> Unit,
    onSetCapacity: (Int) -> Unit,
    onCreateClass: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        SectionBlock(state.selectedSection, onSelectSection)
        OptionBlock(state.selectedOption, onSelectOption)
        CapacityBlock(state.capacity, onSetCapacity)
        ActionsBlock(
            isLoading = state.isLoading,
            onCreate = onCreateClass,
            onCancel = onCancel,
            errorMessage = state.errorMessage
        )
    }
}

@Composable
private fun SectionBlock(
    selectedSection: AcademicSection,
    onSelectSection: (AcademicSection) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("1. Choisir la Section")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AcademicSectionCard(
                label = "Scientifique",
                subtitle = "Sciences exactes et expérimentales",
                isSelected = selectedSection == AcademicSection.Scientifique,
                onClick = { onSelectSection(AcademicSection.Scientifique) }
            )
            AcademicSectionCard(
                label = "Littéraire",
                subtitle = "Lettres, Arts et Langues vivantes",
                isSelected = selectedSection == AcademicSection.Littéraire,
                onClick = { onSelectSection(AcademicSection.Littéraire) }
            )
            AcademicSectionCard(
                label = "Économique",
                subtitle = "Gestion et Sciences Sociales",
                isSelected = selectedSection == AcademicSection.Économique,
                onClick = { onSelectSection(AcademicSection.Économique) }
            )
        }
    }
}

@Composable
private fun OptionBlock(
    selectedOption: AcademicOption,
    onSelectOption: (AcademicOption) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("2. Sélectionner l'Option")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AcademicOptionChip(
                label = "Math & Physique",
                selected = selectedOption == AcademicOption.MathPhysique,
                onClick = { onSelectOption(AcademicOption.MathPhysique) }
            )
            AcademicOptionChip(
                label = "Chimie & Bio",
                selected = selectedOption == AcademicOption.ChimieBio,
                onClick = { onSelectOption(AcademicOption.ChimieBio) }
            )
            AcademicOptionChip(
                label = "Informatique",
                selected = selectedOption == AcademicOption.Informatique,
                onClick = { onSelectOption(AcademicOption.Informatique) }
            )
            AcademicOptionChip(
                label = "Robotique",
                selected = selectedOption == AcademicOption.Robotique,
                onClick = { onSelectOption(AcademicOption.Robotique) }
            )
        }
    }
}

@Composable
private fun CapacityBlock(capacity: Int, onSetCapacity: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("3. Capacité de la Classe")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(24.dp)
        ) {
            Text(
                text = "Nombre de sièges disponibles",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            Slider(
                value = capacity.toFloat(),
                onValueChange = { onSetCapacity(it.toInt()) },
                valueRange = 10f..60f,
                steps = 4
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "MIN : 10", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = "$capacity", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "MAX : 60", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
private fun ActionsBlock(
    isLoading: Boolean,
    onCreate: () -> Unit,
    onCancel: () -> Unit,
    errorMessage: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onCreate,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = if (isLoading) "Création..." else "Créer la classe", fontWeight = FontWeight.SemiBold)
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
            text = "Preview",
            fontSize = 14.sp,
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
                    text = "Classe 102 - ${state.selectedSection.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = "Section",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = state.selectedSection.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        PreviewRow(label = "Option", value = when (state.selectedOption) {
            AcademicOption.MathPhysique -> "Math & Physique"
            AcademicOption.ChimieBio -> "Chimie & Bio"
            AcademicOption.Informatique -> "Informatique"
            AcademicOption.Robotique -> "Robotique"
        })
        PreviewRow(label = "Capacité", value = "${state.capacity} élèves")
        PreviewRow(label = "Responsable", value = "Non assigné")
    }
}

@Composable
private fun PreviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

@Composable
private fun SectionTitle(label: String) {
    Text(
        text = label,
        fontSize = 16.sp,
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
    val background = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val subtitleColor = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .clickable { onClick() }
            .padding(20.dp)
            .width(220.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = AppIcons.school,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
        Text(text = subtitle, fontSize = 13.sp, color = subtitleColor)
    }
}

@Composable
private fun AcademicOptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(text = label, color = textColor, fontSize = 14.sp)
    }
}