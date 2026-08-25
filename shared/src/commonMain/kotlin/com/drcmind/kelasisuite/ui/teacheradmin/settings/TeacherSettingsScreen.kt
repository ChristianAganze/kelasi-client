package com.drcmind.kelasisuite.ui.teacheradmin.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import org.koin.compose.koinInject

@Composable
fun TeacherSettingsScreen(
    modifier: Modifier = Modifier
) {
    val settingsStorage = koinInject<SettingsStorage>()
    val userInfo = settingsStorage.getUserInfo()
    val school = settingsStorage.getSchool()

    var fullName by remember { mutableStateOf(userInfo.displayName.ifEmpty { "Prof. Roger MUKENDI" }) }
    var email by remember { mutableStateOf(userInfo.username ?: "r.mukendi@ecole-excellence.cd") }
    var phone by remember { mutableStateOf("+243 81 234 5678") }
    var teacherId by remember { mutableStateOf("ENS-2026-042") }
    var qualification by remember { mutableStateOf("Licence en Sciences Mathématiques (UNIKIN)") }

    var signaturePin by remember { mutableStateOf("••••") }
    var notifyClassLog by remember { mutableStateOf(true) }
    var notifyParentMessages by remember { mutableStateOf(true) }
    var notifyGradesDeadline by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(isSaved) {
        if (isSaved) {
            snackbarHostState.showSnackbar("Paramètres et préférences enregistrés.")
            isSaved = false
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = fullName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Matricule : $teacherId • Établissement : ${school?.officialName ?: "Complexe Scolaire Excellence"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Section 1: Informations personnelles & Académiques
            item {
                Text(
                    text = "Profil Enseignant & Coordonnées",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Nom complet") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Adresse e-mail / Identifiant") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Numéro de téléphone") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                        )

                        OutlinedTextField(
                            value = qualification,
                            onValueChange = { qualification = it },
                            label = { Text("Titre académique / Qualification") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) }
                        )
                    }
                }
            }

            // Section 2: Sécurité et Signature Électronique
            item {
                Text(
                    text = "Sécurité & Signature Numérique",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Code PIN de signature des fiches de préparation et journaux",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = signaturePin,
                            onValueChange = { signaturePin = it },
                            label = { Text("Code PIN (4 chiffres)") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
                        )
                    }
                }
            }

            // Section 3: Préférences de notifications
            item {
                Text(
                    text = "Notifications & Alertes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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

            item {
                Button(
                    onClick = { isSaved = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enregistrer les modifications")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
