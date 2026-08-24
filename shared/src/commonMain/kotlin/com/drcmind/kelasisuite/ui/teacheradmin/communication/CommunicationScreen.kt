package com.drcmind.kelasisuite.ui.teacheradmin.communication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.drcmind.kelasisuite.data.datasource.remote.dto.ConversationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.MessageDTO
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.components.LoadingState
import com.drcmind.kelasisuite.ui.components.friendlyErrorMessage
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunicationScreen(
    viewModel: CommunicationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpandedOrMedium = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

    var showAttachDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.sendError) {
        if (state.sendError != null) {
            snackbarHostState.showSnackbar(friendlyErrorMessage(state.sendError))
            viewModel.dismissSendError()
        }
    }

    if (showAttachDialog) {
        AttachmentSelectionDialog(
            onDismiss = { showAttachDialog = false },
            onSelectAttachment = { name, type ->
                showAttachDialog = false
                viewModel.sendMessage(
                    content = "Document partagé : $name",
                    attachmentName = name,
                    attachmentType = type
                )
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            if (isExpandedOrMedium) {
                // Dual Pane Layout (Desktop & Tablet)
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Pane: Conversation list (fixed width ~360dp)
                    Surface(
                        modifier = Modifier
                            .width(360.dp)
                            .fillMaxHeight(),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp
                    ) {
                        ConversationListPane(
                            state = state,
                            onSelectConversation = { viewModel.selectConversation(it) },
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onFilterChange = { viewModel.setFilter(it) },
                            onRetry = { viewModel.retryConversations() }
                        )
                    }

                    VerticalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )

                    // Right Pane: Active Chat or Empty state placeholder
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    ) {
                        val selected = state.selectedConversation
                        if (selected != null) {
                            ChatDetailPane(
                                conversation = selected,
                                state = state,
                                isCompact = false,
                                onBackClick = { viewModel.clearSelectedConversation() },
                                onSendMessage = { text -> viewModel.sendMessage(text) },
                                onOpenAttachmentPicker = { showAttachDialog = true },
                                onRetryMessages = { viewModel.retryMessages() }
                            )
                        } else {
                            ChatPlaceholderPane()
                        }
                    }
                }
            } else {
                // Stack Navigation Layout (Mobile / Compact)
                val selected = state.selectedConversation
                if (selected != null) {
                    ChatDetailPane(
                        conversation = selected,
                        state = state,
                        isCompact = true,
                        onBackClick = { viewModel.clearSelectedConversation() },
                        onSendMessage = { text -> viewModel.sendMessage(text) },
                        onOpenAttachmentPicker = { showAttachDialog = true },
                        onRetryMessages = { viewModel.retryMessages() }
                    )
                } else {
                    ConversationListPane(
                        state = state,
                        onSelectConversation = { viewModel.selectConversation(it) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onFilterChange = { viewModel.setFilter(it) },
                        onRetry = { viewModel.retryConversations() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListPane(
    state: CommunicationState,
    onSelectConversation: (ConversationDTO) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (ConversationFilter) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discussions",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(
                            text = "${state.conversations.size}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Rechercher un parent, élève...", style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Rechercher",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Effacer")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ConversationFilter.entries.forEach { filter ->
                        val isSelected = state.activeFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(filter) },
                            label = {
                                Text(
                                    filter.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        // Content
        Box(modifier = Modifier.weight(1f)) {
            when {
                state.isLoading && state.conversations.isEmpty() -> {
                    LoadingState(modifier = Modifier.fillMaxSize())
                }
                state.conversationError != null -> {
                    ErrorStateCard(
                        message = state.conversationError,
                        onRetry = onRetry,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
                state.filteredConversations.isEmpty() -> {
                    EmptyStateCard(
                        title = "Aucune conversation",
                        subtitle = if (state.searchQuery.isNotBlank()) "Aucun résultat pour \"${state.searchQuery}\"" else "Vos discussions avec les parents et l'école s'afficheront ici.",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.filteredConversations, key = { it.id }) { conversation ->
                            ConversationListItem(
                                conversation = conversation,
                                currentUserId = state.currentUserId,
                                isSelected = state.selectedConversation?.id == conversation.id,
                                onClick = { onSelectConversation(conversation) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationListItem(
    conversation: ConversationDTO,
    currentUserId: Long,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val receiverId = conversation.participants.firstOrNull { it != currentUserId }
    val receiverName = receiverId?.let { conversation.participantNames[it] } ?: "Contact"
    val receiverRole = receiverId?.let { conversation.participantRoles[it] } ?: "Élève / Parent"
    val isOnline = receiverId?.let { conversation.participantOnlineStatus[it] } ?: false

    val avatarColor = when (conversation.category) {
        "ADMIN" -> MaterialTheme.colorScheme.tertiary
        "TEACHERS" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with online status
            Box {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = receiverName.take(1).uppercase(),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = receiverName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = formatTimestampShort(conversation.lastMessage?.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (conversation.unreadCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Role tag
                Text(
                    text = receiverRole,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Last Message + Unread Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (conversation.lastMessage?.senderId == currentUserId) {
                            Icon(
                                imageVector = if (conversation.lastMessage.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (conversation.lastMessage.isRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = conversation.lastMessage?.content ?: "Nouvelle conversation",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (conversation.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (conversation.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (conversation.unreadCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = "${conversation.unreadCount}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
        modifier = Modifier.padding(start = 78.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailPane(
    conversation: ConversationDTO,
    state: CommunicationState,
    isCompact: Boolean,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenAttachmentPicker: () -> Unit,
    onRetryMessages: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val receiverId = conversation.participants.firstOrNull { it != state.currentUserId }
    val receiverName = receiverId?.let { conversation.participantNames[it] } ?: "Contact"
    val receiverRole = receiverId?.let { conversation.participantRoles[it] } ?: "Élève / Parent"
    val isOnline = receiverId?.let { conversation.participantOnlineStatus[it] } ?: false

    // Auto-scroll when messages change
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        // Chat Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCompact) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = receiverName.take(1).uppercase(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Role & Status
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = receiverName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = receiverRole,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (isOnline) "En ligne" else "Actif récemment",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOnline) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onOpenAttachmentPicker) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Pièce jointe",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

        // Messages List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            when {
                state.messageError != null -> {
                    ErrorStateCard(
                        message = state.messageError,
                        onRetry = onRetryMessages,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
                state.messages.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Communication sécurisée de l'établissement",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Les messages échangés avec $receiverName sont confidentiels et archivés pour le suivi pédagogique.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    tonalElevation = 1.dp
                                ) {
                                    Text(
                                        text = "Aujourd'hui",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        items(state.messages, key = { it.id ?: (0..100000).random().toLong() }) { message ->
                            WhatsAppMessageBubble(
                                message = message,
                                isMine = message.senderId == state.currentUserId
                            )
                        }
                    }
                }
            }
        }

        // Input Area (WhatsApp Style)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attachment icon
                IconButton(
                    onClick = onOpenAttachmentPicker,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Joindre un document",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Text Input
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Écrire un message...", style = MaterialTheme.typography.bodyMedium) },
                    shape = RoundedCornerShape(26.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                FilledIconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText.trim())
                            messageText = ""
                            coroutineScope.launch {
                                if (state.messages.isNotEmpty()) {
                                    listState.animateScrollToItem(state.messages.size)
                                }
                            }
                        }
                    },
                    enabled = messageText.isNotBlank(),
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Envoyer",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WhatsAppMessageBubble(message: MessageDTO, isMine: Boolean) {
    val bubbleColor = if (isMine) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val contentColor = if (isMine) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val metaColor = if (isMine) {
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val bubbleShape = if (isMine) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 3.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 3.dp, bottomEnd = 16.dp)
    }

    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = bubbleShape,
            tonalElevation = 1.dp,
            modifier = Modifier.widthIn(min = 90.dp, max = 340.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Attached File Preview if any
                if (!message.attachmentName.isNullOrBlank()) {
                    AttachmentCard(
                        name = message.attachmentName,
                        type = message.attachmentType ?: "document",
                        isMine = isMine
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Message Text
                Text(
                    text = message.content,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Time and read status
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTimestampTime(message.timestamp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = metaColor
                    )
                    if (isMine) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                            contentDescription = if (message.isRead) "Lu" else "Envoyé",
                            tint = metaColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentCard(
    name: String,
    type: String,
    isMine: Boolean
) {
    val (icon, badgeLabel, iconColor) = when (type) {
        "bulletin" -> Triple(Icons.Default.Assessment, "Bulletin de notes", Color(0xFFE91E63))
        "discipline" -> Triple(Icons.Default.Warning, "Fiche de conduite", Color(0xFFFF9800))
        "attendance" -> Triple(Icons.Default.FactCheck, "Présence", Color(0xFF2196F3))
        else -> Triple(Icons.Default.Description, "Document PDF", Color(0xFF4CAF50))
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isMine) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$badgeLabel • PDF (420 Ko)",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMine) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AttachmentSelectionDialog(
    onDismiss: () -> Unit,
    onSelectAttachment: (name: String, type: String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Joindre un document pédagogique", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AttachmentOptionRow(
                    icon = Icons.Default.Assessment,
                    title = "Bulletin de notes (Trimestre)",
                    subtitle = "Transmettre le relevé officiel en PDF",
                    iconColor = Color(0xFFE91E63),
                    onClick = {
                        onSelectAttachment("Bulletin_Trimestre_Synthese.pdf", "bulletin")
                    }
                )
                AttachmentOptionRow(
                    icon = Icons.Default.Warning,
                    title = "Fiche de conduite & discipline",
                    subtitle = "Rapport disciplinaire ou mot d'absence",
                    iconColor = Color(0xFFFF9800),
                    onClick = {
                        onSelectAttachment("Fiche_Discipline_Observation.pdf", "discipline")
                    }
                )
                AttachmentOptionRow(
                    icon = Icons.Default.FactCheck,
                    title = "Relevé de présence & retards",
                    subtitle = "Bilan des cours et présences",
                    iconColor = Color(0xFF2196F3),
                    onClick = {
                        onSelectAttachment("Releve_Presences_Mensuel.pdf", "attendance")
                    }
                )
                AttachmentOptionRow(
                    icon = Icons.Default.Description,
                    title = "Autre document / Devoir à domicile",
                    subtitle = "Fiche d'exercices ou circulaire",
                    iconColor = Color(0xFF4CAF50),
                    onClick = {
                        onSelectAttachment("Devoir_Maison_Mathematiques.pdf", "document")
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun AttachmentOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ChatPlaceholderPane() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 420.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Kelasi Messagerie Enseignant",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sélectionnez une discussion dans la liste pour échanger directement avec les parents d'élèves, l'administration ou vos collègues enseignants.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

private fun formatTimestampShort(timestamp: String?): String {
    if (timestamp.isNullOrBlank()) return ""
    return try {
        if (timestamp.length >= 16 && timestamp.contains("T")) {
            val timePart = timestamp.substringAfter("T").take(5)
            timePart
        } else {
            "Aujourd'hui"
        }
    } catch (e: Exception) {
        "Aujourd'hui"
    }
}

private fun formatTimestampTime(timestamp: String?): String {
    if (timestamp.isNullOrBlank()) return "12:00"
    return try {
        if (timestamp.contains("T")) {
            timestamp.substringAfter("T").take(5)
        } else {
            "12:00"
        }
    } catch (e: Exception) {
        "12:00"
    }
}
