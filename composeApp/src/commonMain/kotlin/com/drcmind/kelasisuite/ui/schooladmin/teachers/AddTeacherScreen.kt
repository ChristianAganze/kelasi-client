package com.drcmind.kelasisuite.ui.schooladmin.teachers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.dto.UserDTO
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeacherScreen(
    teacherId: Long? = null,
    onBack: () -> Unit,
    onTeacherAdded: () -> Unit,
    viewModel: AddTeacherViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isEditing = teacherId != null

    LaunchedEffect(teacherId) {
        if (teacherId != null) {
            viewModel.loadTeacher(teacherId)
        }
    }

    if (state.isSuccess) {
        onTeacherAdded()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Modification Enseignant" else if (state.showUserList) "Sélection de l'utilisateur" else "Recrutement Enseignant") },
                navigationIcon = {
                    IconButton(onClick = if (!isEditing && !state.showUserList) viewModel::onBackToUserList else onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.showUserList) {
                UserSelectionSection(
                    users = state.users,
                    isLoading = state.isLoading,
                    error = state.error,
                    searchQuery = state.searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onUserSelected = viewModel::onUserSelected
                )
            } else {
                TeacherFormSection(
                    state = state,
                    teacherId = teacherId,
                    onBack = if (isEditing) onBack else viewModel::onBackToUserList,
                    viewModel = viewModel,
                    isEditing = isEditing
                )
            }
        }
    }
}

@Composable
private fun UserSelectionSection(
    users: List<UserDTO>,
    isLoading: Boolean,
    error: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onUserSelected: (UserDTO) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().maxSize(900.dp)) {
        Text(
            "Étape 1 : Choisir un utilisateur",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "Sélectionnez le compte utilisateur à associer au nouveau profil d'enseignant.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Rechercher un utilisateur...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        } else if (users.isEmpty()) {
            Text(
                if (searchQuery.isEmpty()) "Aucun utilisateur disponible pour le recrutement."
                else "Aucun utilisateur ne correspond à votre recherche.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(users) { user ->
                    UserItemCard(user = user, onClick = { onUserSelected(user) })
                }
            }
        }
    }
}

@Composable
private fun UserItemCard(user: UserDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        user.firstName.take(1) + user.lastName.take(1),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    user.email ?: user.phone ?: "Pas de contact",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun TeacherFormSection(
    state: AddTeacherState,
    teacherId: Long?,
    onBack: () -> Unit,
    viewModel: AddTeacherViewModel,
    isEditing: Boolean
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(modifier = Modifier.fillMaxWidth().maxSize(900.dp)) {
            Text(
                if (isEditing) "Modifier les informations de l'enseignant" else "Étape 2 : Détails Professionnels",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                if (isEditing) "Mettez à jour le profil de ${state.fullName}." else "Veuillez compléter les informations académiques pour ${state.fullName}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (state.selectedUser != null || state.fullName.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth().maxSize(1000.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                state.fullName.take(1),
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Utilisateur associé",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            state.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (!isEditing) {
                        TextButton(onClick = viewModel::onBackToUserList) {
                            Text("Changer")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth().maxSize(1000.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            FlowRow(
                modifier = Modifier.padding(32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Colonne de Gauche : Photo & Matricule
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
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
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "PROFILE PHOTO",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    InfoTag(AppIcons.badge, "ID PAIE / MATRICULE", state.payrollId)
                }

                // Colonne de Droite : Formulaire
                Column(
                    modifier = Modifier.weight(2f).padding(start = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    KelasiInputField(
                        "Nom Complet",
                        state.fullName,
                        "Ex: Prof. Justin Ntwali",
                        onValueChange = viewModel::onFullNameChange
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        KelasiInputField(
                            "Date d'Embauche",
                            state.hireDate,
                            "Ex: 2026-05-11",
                            Modifier.weight(1f),
                            icon = Icons.Default.CalendarToday,
                            onValueChange = viewModel::onHireDateChange
                        )
                        KelasiInputField(
                            "Qualifications / Diplômes",
                            state.qualifications,
                            "Ex: Master en Informatique",
                            Modifier.weight(1f),
                            onValueChange = viewModel::onQualificationsChange
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        KelasiInputField(
                            "Heures Max / Semaine",
                            state.maxWeeklyHours,
                            "Ex: 40",
                            Modifier.weight(1f),
                            onValueChange = viewModel::onMaxHoursChange
                        )
                        KelasiInputField(
                            "Ville / Territoire",
                            state.city,
                            "Ex: Bukavu",
                            Modifier.weight(1f),
                            onValueChange = viewModel::onCityChange
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        KelasiInputField(
                            "Province",
                            state.province,
                            "Ex: Sud-Kivu",
                            Modifier.weight(1f),
                            onValueChange = viewModel::onProvinceChange
                        )
                        KelasiInputField(
                            "Adresse Domicile",
                            state.streetAddress,
                            "Ex: Avenue Irambo, N° 12",
                            Modifier.weight(1f),
                            onValueChange = viewModel::onAddressChange
                        )
                    }

                    if (state.error != null) {
                        Text(
                            text = state.error,
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
                        TextButton(onClick = onBack) {
                            Text("RETOUR", color = MaterialTheme.colorScheme.outline)
                        }
                        Spacer(Modifier.width(16.dp))
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
                                    if (isEditing) "METTRE À JOUR" else "CONFIRMER LE RECRUTEMENT",
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

@Composable
private fun KelasiInputField(
    label: String,
    value: String,
    placeHolderText: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isDropdown: Boolean = false,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = singleLine,
            placeholder = { Text(placeHolderText, style = MaterialTheme.typography.bodyMedium) },
            trailingIcon = {
                if (isDropdown) Icon(Icons.Default.KeyboardArrowDown, null)
                else if (icon != null) Icon(icon, null, modifier = Modifier.size(18.dp))
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun InfoTag(icon: ImageVector, label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

fun Modifier.maxSize(width: Dp) = this.widthIn(max = width)
