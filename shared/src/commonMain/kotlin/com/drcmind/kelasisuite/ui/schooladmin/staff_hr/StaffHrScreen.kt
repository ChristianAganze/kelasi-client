package com.drcmind.kelasisuite.ui.schooladmin.staff_hr

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.EmptyDetailPlaceholder
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun StaffHrScreen(
    viewModel: StaffHrViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.StaffHR.List::class,
                        Route.SchoolAdmin.StaffHR.List.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.StaffHR.Detail::class,
                        Route.SchoolAdmin.StaffHR.Detail.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.StaffHR.List
    )

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(
                horizontalPartitionSpacerSize = 0.dp,
                verticalPartitionSpacerSize = 0.dp,
                defaultPanePreferredWidth = 800.dp
            )
    }
    val listDetailsStrategy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
        directive = directive
    )

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailsStrategy),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.StaffHR.List>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        EmptyDetailPlaceholder(
                            icon = Icons.Default.Badge,
                            title = "Aucun utilisateur sélectionné",
                            subtitle = "Sélectionnez un membre du personnel pour afficher son profil détaillé",
                        )
                    }
                )
            ) {
                Scaffold(
                    topBar = {
                        Column {
                            TopAppBar(
                                title = {
                                    Text(
                                        "Ressources Humaines & Personnel",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                actions = {
                                    IconButton(onClick = { viewModel.loadUsers() }) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                                    }
                                }
                            )

                            // Search bar & Role filters
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SearchBar(
                                    inputField = {
                                        InputField(
                                            query = uiState.searchQuery,
                                            onQueryChange = { viewModel.onSearchQueryChange(it) },
                                            onSearch = { },
                                            expanded = false,
                                            onExpandedChange = { },
                                            placeholder = { Text("Rechercher un membre du personnel...") },
                                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                            trailingIcon = {
                                                if (uiState.searchQuery.isNotEmpty()) {
                                                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                                        Icon(Icons.Default.Clear, contentDescription = "Effacer")
                                                    }
                                                }
                                            }
                                        )
                                    },
                                    expanded = false,
                                    onExpandedChange = { },
                                    modifier = Modifier.weight(1f)
                                ) {}
                            }

                            // Filter chips by Role
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val roles = listOf(
                                    "ALL" to "Tous (${uiState.users.size})",
                                    "TEACHER" to "Enseignants",
                                    "ADMIN" to "Administrateurs",
                                    "PARENT" to "Parents",
                                    "STUDENT" to "Élèves"
                                )
                                items(roles) { (roleKey, label) ->
                                    FilterChip(
                                        selected = uiState.selectedRoleFilter == roleKey,
                                        onClick = { viewModel.onRoleFilterChange(roleKey) },
                                        label = { Text(label) }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (uiState.isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (uiState.error != null && uiState.users.isEmpty()) {
                            ErrorStateCard(
                                message = uiState.error,
                                onRetry = { viewModel.loadUsers() }
                            )
                        } else if (uiState.filteredUsers.isEmpty()) {
                            EmptyStateCard(
                                title = "Aucun utilisateur trouvé",
                                subtitle = if (uiState.searchQuery.isNotEmpty() || uiState.selectedRoleFilter != "ALL") {
                                    "Aucun membre du personnel ne correspond aux critères de recherche actuels."
                                } else {
                                    "Aucun utilisateur n'est encore enregistré dans cet établissement."
                                },
                                icon = Icons.Default.GroupOff
                            )
                        } else {
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = MaterialTheme.shapes.medium.copy(
                                    bottomStart = CornerSize(0.dp),
                                    bottomEnd = CornerSize(0.dp)
                                )
                            ) {
                                Column {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "MEMBRE DU PERSONNEL",
                                            modifier = Modifier.weight(2f),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "RÔLES / FONCTION",
                                            modifier = Modifier.weight(1.5f),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            "STATUT",
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End
                                        )
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                    LazyColumn {
                                        items(uiState.filteredUsers) { user ->
                                            StaffUserRow(
                                                user = user,
                                                isSelected = uiState.selectedUser?.id == user.id,
                                                onClick = {
                                                    viewModel.selectUser(user)
                                                    if (backStack.last() != Route.SchoolAdmin.StaffHR.List) {
                                                        backStack.removeLastOrNull()
                                                    }
                                                    backStack.add(Route.SchoolAdmin.StaffHR.Detail(user.id))
                                                }
                                            )
                                            HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            entry<Route.SchoolAdmin.StaffHR.Detail>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {
                StaffUserDetailPane(
                    user = uiState.selectedUser ?: uiState.users.find { u -> u.id == it.userId },
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}

@Composable
fun StaffUserRow(
    user: UserDTO,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            val initials = "${user.firstName.take(1)}${user.lastName.take(1)}".uppercase()
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials.ifEmpty { "U" },
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.width(14.dp))

            // Names & username
            Column(Modifier.weight(2f)) {
                Text(
                    text = "${user.firstName} ${user.lastName}".trim().ifEmpty { user.username },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "@${user.username}" + (user.phone?.let { " • $it" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Roles
            Box(
                Modifier.weight(1.5f),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    user.roles.forEach { role ->
                        SuggestionChip(
                            onClick = { },
                            label = {
                                Text(
                                    formatRoleName(role),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }

            // Active / Inactive Status
            Box(
                Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            if (user.isActive) "Actif" else "Inactif",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (user.isActive) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun StaffUserDetailPane(
    user: UserDTO?,
    onBack: () -> Unit
) {
    if (user == null) {
        EmptyDetailPlaceholder(
            icon = Icons.Default.PersonOff,
            title = "Utilisateur introuvable",
            subtitle = "Les données de cet utilisateur ne sont pas disponibles."
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fiche Utilisateur") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val initials = "${user.firstName.take(1)}${user.lastName.take(1)}".uppercase()
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials.ifEmpty { "U" },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "${user.firstName} ${user.lastName}".trim().ifEmpty { user.username },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Identifiant : @${user.username}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            user.roles.forEach { role ->
                                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                    Text(formatRoleName(role))
                                }
                            }
                        }
                    }
                }
            }

            // Info details
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Informations de Contact & Compte",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    StaffInfoItem(
                        icon = Icons.Outlined.Email,
                        label = "Adresse E-mail",
                        value = user.email ?: "Non renseignée"
                    )

                    StaffInfoItem(
                        icon = Icons.Outlined.Phone,
                        label = "Téléphone",
                        value = user.phone ?: "Non renseigné"
                    )

                    StaffInfoItem(
                        icon = Icons.Outlined.Shield,
                        label = "Rôles attribués",
                        value = user.roles.joinToString(", ") { formatRoleName(it) }
                    )

                    StaffInfoItem(
                        icon = Icons.Outlined.CheckCircle,
                        label = "Statut du compte",
                        value = if (user.isActive) "Compte actif et autorisé" else "Compte suspendu ou inactif"
                    )
                }
            }
        }
    }
}

@Composable
fun StaffInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatRoleName(role: String): String {
    return when (role.uppercase()) {
        "ROLE_TEACHER", "TEACHER" -> "Enseignant"
        "ROLE_ADMIN", "ADMIN" -> "Administrateur"
        "ROLE_PARENT", "PARENT" -> "Parent"
        "ROLE_STUDENT", "STUDENT" -> "Élève"
        "ROLE_SUPER_ADMIN" -> "Super Admin"
        else -> role.replace("ROLE_", "").lowercase().replaceFirstChar { it.uppercase() }
    }
}
