package com.drcmind.kelasisuite.ui.schooladmin.communication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.model.communication.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolAdminCommunicationScreen(
    viewModel: SchoolAdminCommunicationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Communication & Relations École-Parents", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Diffusion officielle, circulaires, réunions et messagerie",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.openCreateDialog() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nouveau Communiqué")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            // Tabs
            PrimaryTabRow(
                selectedTabIndex = uiState.activeTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                AdminCommTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.activeTab == tab,
                        onClick = { viewModel.setTab(tab) },
                        text = { Text(tab.label, fontWeight = if (uiState.activeTab == tab) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (uiState.activeTab) {
                    AdminCommTab.ANNOUNCEMENTS -> AnnouncementsSplitView(
                        uiState = uiState,
                        onSelectAnnouncement = { viewModel.selectAnnouncement(it) },
                        onTogglePin = { viewModel.togglePin(it) }
                    )
                    AdminCommTab.DIRECT_MESSAGING -> DirectMessagingSplitView(
                        uiState = uiState,
                        onSelectConversation = { viewModel.selectConversation(it) },
                        onUpdateReplyText = { viewModel.updateReplyText(it) },
                        onSendReply = { viewModel.sendReply() }
                    )
                }
            }
        }
    }

    // Modal Nouveau Communiqué
    if (uiState.isCreateDialogOpen) {
        CreateAnnouncementDialog(
            form = uiState.formState,
            isSubmitting = uiState.isSubmitting,
            onDismiss = { viewModel.closeCreateDialog() },
            onUpdate = { viewModel.updateForm(it) },
            onSubmit = { viewModel.submitAnnouncement() }
        )
    }
}

// -------------------------------------------------------------
// 1. VUE COMMUNIQUÉS OFFICIELS
// -------------------------------------------------------------
@Composable
fun AnnouncementsSplitView(
    uiState: SchoolAdminCommUiState,
    onSelectAnnouncement: (SchoolOfficialAnnouncement) -> Unit,
    onTogglePin: (Long) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Liste à Gauche
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.announcements, key = { it.id }) { ann ->
                    val isSelected = uiState.selectedAnnouncement?.id == ann.id
                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAnnouncement(ann) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(ann.priority.badgeColorHex).copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (ann.isPinned) Icons.Default.Bookmark else Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = Color(ann.priority.badgeColorHex),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = ann.type.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(ann.priority.badgeColorHex)
                                    )
                                    Text(
                                        text = ann.publishedDate.split(" ").firstOrNull() ?: "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ann.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ann.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                }
            }
        }

        // Détail du Communiqué à Droite
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1.3f).fillMaxHeight()
        ) {
            val selected = uiState.selectedAnnouncement
            if (selected != null) {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(selected.priority.badgeColorHex).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${selected.type.label} • ${selected.priority.label}",
                                color = Color(selected.priority.badgeColorHex),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(onClick = { onTogglePin(selected.id) }) {
                            Icon(
                                imageVector = if (selected.isPinned) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Épingler",
                                tint = if (selected.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(selected.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Publié le ${selected.publishedDate} par ${selected.publishedBy} • Audience : ${selected.audience.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        item {
                            Text(
                                text = selected.content,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp
                            )
                        }

                        if (selected.attachmentName != null) {
                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                                OutlinedCard(
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(selected.attachmentName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                        FilledTonalButton(onClick = {}) {
                                            Text("Ouvrir")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Signé : ${selected.signedByTitle}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sélectionnez un communiqué à afficher", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. VUE MESSAGERIE DIRECTE PARENTS
// -------------------------------------------------------------
@Composable
fun DirectMessagingSplitView(
    uiState: SchoolAdminCommUiState,
    onSelectConversation: (DirectParentMessage) -> Unit,
    onUpdateReplyText: (String) -> Unit,
    onSendReply: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Liste des Parents
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.parentConversations, key = { it.id }) { conv ->
                    val isSelected = uiState.selectedConversation?.id == conv.id
                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectConversation(conv) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = conv.parentName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(conv.parentName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text(conv.timestamp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("Parent de ${conv.studentName} (${conv.classroomName})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text(conv.lastMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                }
            }
        }

        // Fil de Discussion & Réponse Rapide
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1.3f).fillMaxHeight()
        ) {
            val conv = uiState.selectedConversation
            if (conv != null) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // Header discussion
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(conv.parentName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Élève : ${conv.studentName} • ${conv.classroomName} • Tél : ${conv.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Message Reçu
                    Box(modifier = Modifier.weight(1f)) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(conv.parentName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(conv.lastMessage, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }

                    // Zone de Réponse
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.replyMessageText,
                            onValueChange = onUpdateReplyText,
                            placeholder = { Text("Répondre au parent...") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onSendReply,
                            modifier = Modifier.background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Envoyer", tint = Color.White)
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sélectionnez une conversation", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG NOUVEAU COMMUNIQUÉ
// -------------------------------------------------------------
@Composable
fun CreateAnnouncementDialog(
    form: AnnouncementFormState,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (AnnouncementFormState.() -> AnnouncementFormState) -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Diffuser un Communiqué Officiel", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = form.title,
                    onValueChange = { onUpdate { copy(title = it) } },
                    label = { Text("Titre du Communiqué") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.summary,
                    onValueChange = { onUpdate { copy(summary = it) } },
                    label = { Text("Résumé court / Objet") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.content,
                    onValueChange = { onUpdate { copy(content = it) } },
                    label = { Text("Texte intégral du communiqué") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Priorité :", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            AnnouncementPriority.entries.forEach { p ->
                                FilterChip(
                                    selected = form.priority == p,
                                    onClick = { onUpdate { copy(priority = p) } },
                                    label = { Text(p.name, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onSubmit, enabled = !isSubmitting) {
                if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else Text("Publier & Diffuser")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
