package com.drcmind.kelasisuite.ui.schooladmin.parents

import androidx.compose.foundation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.domain.dto.*
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ParentsScreen(
    viewModel: ParentsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddParentDialog by remember { mutableStateOf(false) }
    var parentToEdit by remember { mutableStateOf<ParentDto?>(null) }

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Parents.List::class,
                        Route.SchoolAdmin.Parents.List.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Parents.Profile::class,
                        Route.SchoolAdmin.Parents.Profile.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Parents.List
    )
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp, verticalPartitionSpacerSize = 0.dp)
    }
    val listDetailsStrateggy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
        directive = directive
    )

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailsStrateggy),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.Parents.List>(
                metadata = ListDetailSceneStrategy.listPane()
            ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = Color.Transparent,
                                navigationIconContentColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme
                                    .onSurface,
                                actionIconContentColor = Color.Transparent,
                            ),

                            title = {
                                Text(
                                    text = "Gestion des parents",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight =
                                            FontWeight.Black
                                    )
                                )

                            },
                            actions = {
                                IconButton(onClick = viewModel::loadSchoolParent) {
                                    Icon(
                                        AppIcons.refresh,
                                        contentDescription = "Actualiser",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                ElevatedButton(
                                    colors = ButtonDefaults.buttonColors(),
                                    onClick = {
                                        parentToEdit = null
                                        showAddParentDialog = true
                                    }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)

                                }
                            }
                        )
                    },

                    ) {

                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(it),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        ParentTable(
                            viewModel = viewModel,
                            modifier = Modifier.padding(it).padding(horizontal = 16.dp),
                            parents = uiState.list,
                            onNavigateToParentProfile = { id ->
                                backStack.add(Route.SchoolAdmin.Parents.Profile(id))
                            },
                            onEditParent = { parent ->
                                parentToEdit = parent
                                showAddParentDialog = true
                            }
                        )
                    }
                }
            }
            entry<Route.SchoolAdmin.Parents.Profile>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {
                ParentDetailPane(
                    parentId = it.parentId,
                    viewModel = viewModel,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )

    val users by viewModel.users.collectAsState()
    val students by viewModel.students.collectAsState()
    val academicYears by viewModel.academicYears.collectAsState()

    if (showAddParentDialog) {
        ParentFormDialog(
            parent = parentToEdit,
            users = users,
            onDismiss = { showAddParentDialog = false },
            onConfirm = { userId, address, occupation ->
                if (parentToEdit == null) {
                    viewModel.createParent(userId, address, occupation) {
                        showAddParentDialog = false
                    }
                } else {
                    viewModel.updateParent(parentToEdit!!.id!!, userId, address, occupation) {
                        showAddParentDialog = false
                    }
                }
            }
        )
    }
}

@Composable
fun ParentTable(
    modifier: Modifier = Modifier,
    parents: List<ParentDto>,
    onNavigateToParentProfile: (Long) -> Unit,
    viewModel: ParentsViewModel, onEditParent: (ParentDto) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier.fillMaxWidth().clip(
            MaterialTheme.shapes.extraLarge
        ).background(MaterialTheme.colorScheme.surface).border(
            shape = MaterialTheme.shapes.extraLarge,
            border =
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        )
    ) {
        TextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier.fillMaxWidth().padding(16.dp).widthIn(max = 400.dp),
            placeholder = {
                Text(
                    "Rechercher un parent (Nom, ID)...",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,    // Supprime la ligne sous le champ (focus)
                unfocusedIndicatorColor = Color.Transparent,  // Supprime la ligne sous le champ
                disabledIndicatorColor = Color.Transparent,
            )
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Noms",
                modifier = Modifier.weight(2f),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                "Occupation",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                "Étudiants liés",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                "Actions",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        if (parents.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    AppIcons.peoples,
                    contentDescription = null,
                    Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(Modifier.height(10.dp))
                Text("Aucun parent trouvé", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn {
                items(parents.size) { index ->
                    val parent = parents[index]
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ParentRow(
                        parent = parent,
                        onClick = { onNavigateToParentProfile(parent.id!!) },
                        onEdit = { onEditParent(parent) }
                    )
                }
            }
        }
    }
}

@Composable
fun ParentRow(
    parent: ParentDto,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(2f)) {
            Box(
                Modifier.size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    parent.fullName.take(1),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    parent.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    parent.address ?: "Pas d'adresse",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Text(
            parent.occupation ?: "N/A",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (parent.linkages.isEmpty()) {
                Text(
                    "Aucun",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy((-12).dp)) {
                    parent.linkages.take(3).forEach { linkage ->
                        CircularProfile(linkage.student.fullName.take(1))
                    }
                    if (parent.linkages.size > 3) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, Color.White, CircleShape)
                        ) {
                            Text(
                                "+${parent.linkages.size - 3}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
            IconButton(onClick = onEdit) {
                Icon(
                    AppIcons.edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ParentDetailPane(
    parentId: Long,
    viewModel: ParentsViewModel,
    onBack: () -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()
    var showLinkDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var linkageToUnlink by remember { mutableStateOf<ParentStudentLinkageDto?>(null) }

    LaunchedEffect(parentId) {
        viewModel.loadParentDetail(parentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails du parent") },
                actions = {
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            showDeleteConfirmation = true
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (detailState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (detailState.parent != null) {
            val parent = detailState.parent!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Info Card
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(80.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                parent.fullName.take(1),
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(Modifier.width(24.dp))
                        Column {
                            Text(
                                parent.fullName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                parent.occupation ?: "Pas d'occupation",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                parent.address ?: "Pas d'adresse",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Linkages Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Étudiants liés",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = { showLinkDialog = true }) {
                        Icon(Icons.Default.Link, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Lier un étudiant")
                    }
                }

                if (parent.linkages.isEmpty()) {
                    Box(
                        Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aucun étudiant lié", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    parent.linkages.forEach { linkage ->
                        LinkageRow(
                            linkage,
                            onUnlink = { 
                                linkageToUnlink = linkage
                            })
                    }
                }
            }
        }
    }

    val students by viewModel.students.collectAsState()
    val academicYears by viewModel.academicYears.collectAsState()

    if (showLinkDialog) {
        LinkStudentDialog(
            students = students,
            academicYears = academicYears,
            onDismiss = { showLinkDialog = false },
            onConfirm = { studentId, yearId, relType, isPayer ->
                viewModel.linkStudent(parentId, studentId, yearId, relType, isPayer)
                showLinkDialog = false
            }
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Supprimer le parent") },
            text = { Text("Êtes-vous sûr de vouloir supprimer ce parent ? Cette action est irréversible.") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        viewModel.deleteParent(parentId) { onBack() }
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    if (linkageToUnlink != null) {
        AlertDialog(
            onDismissRequest = { linkageToUnlink = null },
            title = { Text("Délier l'étudiant") },
            text = { Text("Voulez-vous vraiment délier l'étudiant ${linkageToUnlink?.student?.fullName} de ce parent ?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        viewModel.unlinkStudent(parentId, linkageToUnlink!!.id!!)
                        linkageToUnlink = null
                    }
                ) {
                    Text("Délier")
                }
            },
            dismissButton = {
                TextButton(onClick = { linkageToUnlink = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun LinkageRow(linkage: ParentStudentLinkageDto, onUnlink: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(linkage.student.fullName.take(1), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(linkage.student.fullName, fontWeight = FontWeight.Bold)
                Text(
                    "${linkage.relationshipType} • ${if (linkage.isPrimaryPayer) "Payeur principal" else "Non payeur"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onUnlink) {
                Icon(
                    Icons.Default.LinkOff,
                    contentDescription = "Unlink",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentFormDialog(
    parent: ParentDto?,
    users: List<UserDTO>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String) -> Unit
) {
    var selectedUserId by remember { mutableStateOf(parent?.userId) }
    var address by remember { mutableStateOf(parent?.address ?: "") }
    var occupation by remember { mutableStateOf(parent?.occupation ?: "") }
    var userExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (parent == null) "Ajouter un parent" else "Modifier un parent") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = userExpanded,
                    onExpandedChange = { userExpanded = it }
                ) {
                    OutlinedTextField(
                        value = users.find { it.id == selectedUserId }
                            ?.let { "${it.firstName} ${it.lastName}" }
                            ?: "Sélectionner un utilisateur",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Utilisateur") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(userExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = MaterialTheme.shapes.large
                    )
                    ExposedDropdownMenu(
                        expanded = userExpanded,
                        onDismissRequest = { userExpanded = false }
                    ) {
                        users.forEach { user ->
                            DropdownMenuItem(
                                text = { Text("${user.firstName} ${user.lastName}") },
                                onClick = {
                                    selectedUserId = user.id
                                    userExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )
                OutlinedTextField(
                    value = occupation,
                    onValueChange = { occupation = it },
                    label = { Text("Occupation") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )
            }
        },
        confirmButton = {
            Button(
                enabled = selectedUserId != null && address.isNotBlank() && occupation.isNotBlank(),
                onClick = { onConfirm(selectedUserId!!, address, occupation) }
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkStudentDialog(
    students: List<StudentDTO>,
    academicYears: List<AcademicYearDTO>,
    onDismiss: () -> Unit,
    onConfirm: (Long, Long, String, Boolean) -> Unit
) {
    var selectedStudentId by remember { mutableStateOf<Long?>(null) }
    var selectedYearId by remember { mutableStateOf<Long?>(academicYears.find { it.isActive }?.id) }
    var relationshipType by remember { mutableStateOf("") }
    var isPrimaryPayer by remember { mutableStateOf(false) }

    var studentExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lier un étudiant") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = studentExpanded,
                    onExpandedChange = { studentExpanded = it }
                ) {
                    OutlinedTextField(
                        value = students.find { it.id == selectedStudentId }?.fullName
                            ?: "Sélectionner un étudiant",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Étudiant") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(studentExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = MaterialTheme.shapes.large
                    )
                    ExposedDropdownMenu(
                        expanded = studentExpanded,
                        onDismissRequest = { studentExpanded = false }
                    ) {
                        students.forEach { student ->
                            DropdownMenuItem(
                                text = { Text(student.fullName) },
                                onClick = {
                                    selectedStudentId = student.id
                                    studentExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = yearExpanded,
                    onExpandedChange = { yearExpanded = it }
                ) {
                    OutlinedTextField(
                        value = academicYears.find { it.id == selectedYearId }?.label
                            ?: "Sélectionner une année",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Année Académique") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(yearExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = MaterialTheme.shapes.large
                    )
                    ExposedDropdownMenu(
                        expanded = yearExpanded,
                        onDismissRequest = { yearExpanded = false }
                    ) {
                        academicYears.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.label) },
                                onClick = {
                                    selectedYearId = year.id
                                    yearExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = relationshipType,
                    onValueChange = { relationshipType = it },
                    label = { Text("Type de relation (ex: Père, Mère)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPrimaryPayer, onCheckedChange = { isPrimaryPayer = it })
                    Text("Payeur principal")
                }
            }
        },
        confirmButton = {
            Button(
                enabled = selectedStudentId != null && selectedYearId != null && relationshipType.isNotBlank(),
                onClick = {
                    onConfirm(
                        selectedStudentId!!,
                        selectedYearId!!,
                        relationshipType,
                        isPrimaryPayer
                    )
                }
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun CircularProfile(
    text: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(1.dp, Color.White, CircleShape)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}