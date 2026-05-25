package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teachers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeacherDialog(
    teacherId: Long? = null,
    onBack: () -> Unit,
    onTeacherAdded: () -> Unit,
    viewModel: TeachersViewModel = koinViewModel()
) {
    val state by viewModel.formState.collectAsState()
    val isEditing = teacherId != null

    LaunchedEffect(teacherId) {
        if (teacherId != null) {
            viewModel.prepareFormForEdit(teacherId)
        } else {
            viewModel.resetForm()
        }
    }

    if (state.isSuccess) {
        onTeacherAdded()
    }

    OutlinedCard() {
        var expanded by rememberSaveable { mutableStateOf(false) }
        TopAppBar(
            title = {
                Text(if (isEditing) "Modification Enseignant" else "Nouvel Enseignant")
            },
            navigationIcon = {
                IconButton(onClick = if (!isEditing && !state.showUserList) viewModel::onBackToUserList else onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                }
            }
        )

        Column(
            modifier = Modifier
                .padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .semantics { isTraversalGroup = true }
            ) {
                SearchBar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .semantics { traversalIndex = 0f },
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = state.searchQuery,
                            onQueryChange = { viewModel.onUserSearchQueryChange(it) },
                            onSearch = {
                                viewModel.onUserSearchQueryChange(state.searchQuery)
                                expanded = false
                            },
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            placeholder = {
                                Text(
                                    "Rechercher l'utilisateur à associer",
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            trailingIcon = {
                                if (state.searchQuery.isNotBlank()) {
                                    IconButton(onClick = { viewModel.onUserSearchQueryChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "")
                                    }
                                }
                            }
                        )
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                )
                {
                    // Display search results in a scrollable column
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        if (state.isLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (state.error != null) {
                            Text(
                                state.error.toString(),
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        } else if (state.users.isEmpty()) {
                            Text(
                                if (state.searchQuery.isEmpty()) "Aucun utilisateur disponible pour le recrutement."
                                else "Aucun utilisateur ne correspond à votre recherche.",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            state.users.forEach { result ->
                                ListItem(
                                    headlineContent = { Text(result.firstName + " " + result.lastName) },
                                    supportingContent = {
                                        Text(result.username)
                                    },
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.onUserSearchQueryChange(result.firstName + " " + result.lastName)
                                            viewModel.onUserSelected(result)
                                            expanded = false
                                        }
                                        .fillMaxWidth()
                                )
                            }
                        }

                    }
                }

            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                // Colonne de Gauche : Photo & Matricule
                Column(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                MaterialTheme.shapes.large
                            )
                            .clickable { /* Picker image */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                AppIcons.addPhoto,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "PROFILE PHOTO",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    AssistChip(
                        onClick = {},
                        label = {
                            Column {
                                Text("ID PAIE / MATRICULE")
                                Text(state.payrollId, style = MaterialTheme.typography.labelMedium)
                            }
                        },
                        leadingIcon = { Icon(Icons.Default.Badge, null) }
                    )
                }

                // Colonne de Droite : Formulaire
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.fullName,
                        onValueChange = { viewModel.onFullNameChange(it) },
                        label = { Text("Nom complet") },
                        placeholder = { Text("Ex: Prof. Justin Ntwali") },
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = state.hireDate,
                            onValueChange = viewModel::onHireDateChange,
                            label = { Text("Date d'Embauche") },
                            placeholder = { Text("Date d'Embauche") },
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = state.qualifications,
                            onValueChange = viewModel::onQualificationsChange,
                            label = { Text("Qualifications / Diplômes") },
                            placeholder = { Text("Ex: Master en Informatique") },
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = state.maxWeeklyHours,
                            onValueChange = viewModel::onMaxHoursChange,
                            label = { Text("Heures Max / Semaine") },
                            placeholder = { Text("Ex: 40") },
                        )

                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = state.city,
                            onValueChange = viewModel::onCityChange,
                            label = { Text("Ville / Territoire") },
                            placeholder = { Text("Ex: Bukavu") },
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = state.province,
                            onValueChange = viewModel::onProvinceChange,
                            label = { Text("Province") },
                            placeholder = { Text("Ex: Sud-Kivu") },
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = state.streetAddress,
                            onValueChange = viewModel::onAddressChange,
                            label = { Text("Adresse Domicile") },
                            placeholder = { Text("Ex: Avenue Irambo, N° 12") },
                        )
                    }

                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Boutons d'Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.saveTeacher(teacherId) },
                            enabled = !state.isLoading,
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.height(52.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.HowToReg,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (isEditing) "Mettre à jour" else "Enregistrer",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

            }

        }

    }
}
