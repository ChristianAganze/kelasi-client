package com.drcmind.kelasisuite.ui.teacheradmin.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.components.LoadingState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherSettingsScreen(
    viewModel: TeacherSettingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val userInfo = uiState.userInfo
    val userProfile = uiState.userProfile
    val school = uiState.school

    var fullName by remember(userProfile, userInfo) {
        mutableStateOf(
            userProfile?.let { "${it.firstName} ${it.lastName}".trim() }?.ifEmpty { null }
                ?: userInfo?.displayName?.ifEmpty { null }
                ?: "Prof. Roger MUKENDI"
        )
    }

    var email by remember(userProfile, userInfo) {
        mutableStateOf(
            userProfile?.email?.ifEmpty { null }
                ?: userProfile?.username
                ?: userInfo?.username
                ?: "r.mukendi@ecole-excellence.cd"
        )
    }

    var phone by remember(userProfile) {
        mutableStateOf(userProfile?.phone ?: "+243 81 234 5678")
    }

    var teacherId by remember(userInfo, userProfile) {
        val id = userProfile?.id ?: userInfo?.userId ?: 42
        mutableStateOf("ENS-2026-${id.toString().padStart(3, '0')}")
    }

    var qualification by remember { mutableStateOf("Licence en Sciences / Pédagogie Appliquée (UNIKIN)") }
    var signaturePin by remember { mutableStateOf("••••") }
    var notifyClassLog by remember { mutableStateOf(true) }
    var notifyParentMessages by remember { mutableStateOf(true) }
    var notifyGradesDeadline by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Profil et préférences mis à jour avec succès.")
            viewModel.resetSaveSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Carte Profil GDE High-End
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            )
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Badge
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val initials = fullName.split(" ")
                                        .mapNotNull { it.firstOrNull()?.toString() }
                                        .take(2)
                                        .joinToString("")
                                        .uppercase()

                                    if (initials.isNotEmpty()) {
                                        Text(
                                            text = initials,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.AccountCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = fullName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = userProfile?.roles?.firstOrNull() ?: userInfo?.role ?: "ENSEIGNANT",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Matricule : $teacherId • ID Utilisateur : #${userProfile?.id ?: userInfo?.userId ?: "Connecté"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "Établissement : ${school?.officialName ?: "Complexe Scolaire Excellence"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (uiState.isLoading) {
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }

            if (uiState.error != null) {
                item {
                    ErrorStateCard(
                        message = uiState.error,
                        onRetry = { viewModel.loadUserProfile() }
                    )
                }
            }

            // Section 1: Informations Personnelles & Coordonnées (Synchronisées Backend)
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Badge,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Profil & Coordonnées Réelles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Nom et Prénom") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Adresse e-mail / Nom d'utilisateur") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Numéro de téléphone") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = qualification,
                            onValueChange = { qualification = it },
                            label = { Text("Titre académique / Diplôme") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Outlined.School, contentDescription = null) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Section 2: Sécurité & Signature Numérique
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sécurité & Signature Numérique",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Code PIN pour visa et signature des fiches de préparation & journaux",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = signaturePin,
                            onValueChange = { signaturePin = it },
                            label = { Text("Code PIN de validation (4 chiffres)") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Section 3: Préférences de notifications
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notifications & Alertes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Visas et signatures de la Direction", fontWeight = FontWeight.SemiBold)
                                Text("Alerte dès qu'une fiche ou un journal est visé ou rejeté", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Switch(checked = notifyClassLog, onCheckedChange = { notifyClassLog = it })
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Messages des Parents d'élèves", fontWeight = FontWeight.SemiBold)
                                Text("Notification instantanée pour chaque nouvelle question", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Switch(checked = notifyParentMessages, onCheckedChange = { notifyParentMessages = it })
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Rappels d'échéances des cotes", fontWeight = FontWeight.SemiBold)
                                Text("Alertes avant la clôture de chaque période d'évaluation", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Switch(checked = notifyGradesDeadline, onCheckedChange = { notifyGradesDeadline = it })
                        }
                    }
                }
            }

            // Bouton de sauvegarde
            item {
                Button(
                    onClick = {
                        viewModel.saveLocalSettings(
                            fullName = fullName,
                            phone = phone,
                            qualification = qualification
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enregistrer les modifications", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

