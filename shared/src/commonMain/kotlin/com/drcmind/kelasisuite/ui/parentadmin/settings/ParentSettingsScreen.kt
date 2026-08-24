package com.drcmind.kelasisuite.ui.parentadmin.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentSettingsScreen(
    viewModel: ParentSettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    androidx.compose.runtime.LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Paramètres & Profil Parent", fontWeight = FontWeight.Bold)
                        Text(
                            "Gestion des coordonnées, alertes scolaires et sécurité",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // SECTION 1: PROFIL RESPONSABLE LÉGAL
            item {
                Text(
                    text = "Profil du Responsable Légal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar Parent",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${state.profile.firstName} ${state.profile.lastName}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${state.profile.relationship} • ${state.profile.profession}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { viewModel.openEditProfile() }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SettingInfoRow(Icons.Default.Phone, "Téléphone principal :", state.profile.phone)
                            SettingInfoRow(Icons.Default.ContactPhone, "Numéro d'urgence :", state.profile.secondaryPhone)
                            SettingInfoRow(Icons.Default.Email, "Adresse e-mail :", state.profile.email)
                            SettingInfoRow(Icons.Default.Home, "Adresse de résidence :", state.profile.address)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { viewModel.openEditProfile() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mettre à jour mes coordonnées")
                        }
                    }
                }
            }

            // SECTION 2: ENFANTS RATTACHÉS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Élèves & Enfants Rattachés",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = { viewModel.openLinkChildDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rattacher", fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(state.associatedChildren) { child ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(child.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("${child.className} • ${child.matricule}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(child.schoolName, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = child.status,
                                color = Color(0xFF2E7D32),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // SECTION 3: ALERTES & NOTIFICATIONS
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Alertes & Notifications en Direct",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Types d'alertes scolaires",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        SettingSwitchItem(
                            title = "Pointage des présences en temps réel",
                            description = "Alerte immédiate dès que l'enfant arrive à l'école ou est signalé absent/retard.",
                            checked = state.notifyAttendanceImmediate,
                            onCheckedChange = { viewModel.toggleAttendanceNotification(it) }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        SettingSwitchItem(
                            title = "Rappels des devoirs & travaux",
                            description = "Notification 24h avant l'échéance d'un devoir à rendre.",
                            checked = state.notifyHomeworkAlerts,
                            onCheckedChange = { viewModel.toggleHomeworkNotification(it) }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        SettingSwitchItem(
                            title = "Publication des notes & bulletins",
                            description = "Avis dès qu'une cote d'interrogation, d'examen ou un bulletin semestriel est publié.",
                            checked = state.notifyGradeReports,
                            onCheckedChange = { viewModel.toggleGradeReports(it) }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        SettingSwitchItem(
                            title = "Échéances financières & minerval",
                            description = "Rappels d'échéances des frais scolaires et confirmation instantanée des quittances.",
                            checked = state.notifyFinanceDueDates,
                            onCheckedChange = { viewModel.toggleFinanceDueDates(it) }
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Canaux de réception",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = state.channelPush,
                                onClick = { viewModel.toggleChannelPush(!state.channelPush) },
                                label = { Text("Push App") },
                                leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = state.channelSms,
                                onClick = { viewModel.toggleChannelSms(!state.channelSms) },
                                label = { Text("SMS Direct") },
                                leadingIcon = { Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = state.channelEmail,
                                onClick = { viewModel.toggleChannelEmail(!state.channelEmail) },
                                label = { Text("E-mail") },
                                leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // SECTION 4: SÉCURITÉ & AUTHENTIFICATION
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sécurité & Code d'Autorisation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        SettingSwitchItem(
                            title = "Code PIN d'autorisation rapide",
                            description = "Exigé pour signer les justifications d'absence et approuver les paiements Mobile Money.",
                            checked = state.isQuickPinEnabled,
                            onCheckedChange = { viewModel.toggleQuickPin(it) }
                        )

                        if (state.isQuickPinEnabled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Code PIN actif : • • • •", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                TextButton(onClick = { viewModel.openChangePinDialog() }) {
                                    Text("Modifier le code PIN")
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        SettingSwitchItem(
                            title = "Déverrouillage biométrique",
                            description = "Connexion et validation par empreinte digitale ou reconnaissance faciale.",
                            checked = state.isBiometricEnabled,
                            onCheckedChange = { viewModel.toggleBiometric(it) }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        OutlinedButton(
                            onClick = { viewModel.openChangePasswordDialog() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Password, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Changer le mot de passe du compte")
                        }
                    }
                }
            }

            // SECTION 5: PRÉFÉRENCES GÉNÉRALES
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Préférences de l'Application",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Language selection
                        Text(
                            text = "Langue de l'interface",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        val languages = listOf("Français", "Swahili", "Lingala", "English")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            languages.forEach { lang ->
                                val isSelected = state.selectedLanguage == lang
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setLanguage(lang) },
                                    label = { Text(lang, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        SettingSwitchItem(
                            title = "Mode Économie de données",
                            description = "Réduit la consommation Internet lors du téléchargement des bulletins et devoirs.",
                            checked = state.isDataSaver,
                            onCheckedChange = { viewModel.toggleDataSaver(it) }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        // App Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Kelasi Suite Parent", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("Version 2.4.0 (Build 2026.08)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    "À jour",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal dialogs
    if (state.isEditingProfile) {
        EditProfileDialog(
            currentProfile = state.profile,
            onDismiss = { viewModel.closeEditProfile() },
            onSave = { updated -> viewModel.saveProfile(updated) }
        )
    }

    if (state.isLinkingChildDialog) {
        LinkChildDialog(
            onDismiss = { viewModel.closeLinkChildDialog() },
            onSubmit = { code, matricule, name, className ->
                viewModel.linkNewChild(code, matricule, name, className)
            }
        )
    }

    if (state.isChangePinDialog) {
        ChangePinDialog(
            currentPin = state.quickPinCode,
            onDismiss = { viewModel.closeChangePinDialog() },
            onSave = { newPin -> viewModel.saveNewPin(newPin) }
        )
    }

    if (state.isChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { viewModel.closeChangePasswordDialog() },
            onSave = { viewModel.saveNewPassword() }
        )
    }
}

@Composable
private fun SettingInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(140.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SettingSwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
