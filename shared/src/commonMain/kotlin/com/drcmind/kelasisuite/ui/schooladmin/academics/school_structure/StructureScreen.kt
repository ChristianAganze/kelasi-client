package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.drcmind.kelasisuite.domain.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.domain.model.SchoolTreeNode
import com.drcmind.kelasisuite.domain.util.NodeType
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StructureScreen(
    viewModel: SchoolStructureViewModel = koinViewModel(),
    schoolStructureBackStack: NavBackStack<NavKey>
) {
    val visibleNodes by viewModel.visibleNodes
    val uiState by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Structure de l'École",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight =
                                    FontWeight.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Visualisez et gérez l'organisation structurelle de votre établissement.",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {


            item {
                StatsGrid(uiState)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,

                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        visibleNodes.forEachIndexed { index, visibleNode ->
                            TreeNodeRow(
                                visibleNode = visibleNode,
                                onToggle = { viewModel.onToggle(it) },
                                onAction = { node, action -> viewModel.onAction(node, action) },
                                onNavigateToClassDetails = {
                                    schoolStructureBackStack.add(
                                        Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail(
                                            visibleNode.node.originalId
                                        )
                                    )
                                }
                            )
                            if (index < visibleNodes.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 24.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }

                        if (visibleNodes.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun StatsGrid(state: ClassesState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Cycles", state.totalCycles.toString(), "Académiques", Modifier.weight(1f))
        StatCard("Sections", state.totalSections.toString(), "Filières", Modifier.weight(1f))
        StatCard("Niveaux", state.totalGradeLevels.toString(), "Grades", Modifier.weight(1f))
        StatCard("Classes", state.totalClasses.toString(), "Effectif total", Modifier.weight(1f))
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TreeNodeRow(
    visibleNode: VisibleNode,
    onToggle: (SchoolTreeNode) -> Unit,
    onAction: (SchoolTreeNode, NodeAction) -> Unit,
    onNavigateToClassDetails: () -> Unit,
) {
    var isUpdateSchoolClassDialogOpen by remember { mutableStateOf(false) }
    val node = visibleNode.node

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = node.type == NodeType.CLASSROOM) { onNavigateToClassDetails() }
            .padding(vertical = 12.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indentation and Expand/Collapse Icon
        Row(
            modifier = Modifier.width((visibleNode.depth * 32 + 40).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (visibleNode.depth > 1) {
                repeat(visibleNode.depth - 1) {
                    Spacer(modifier = Modifier.width(32.dp))
                }
            }

            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    node.loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    node.type != NodeType.CLASSROOM -> {
                        IconButton(onClick = { onToggle(node) }) {
                            Icon(
                                imageVector = if (node.expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    else -> {
                        Icon(
                            imageVector = AppIcons.school,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Title and Info
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = node.title,
                    style = if (node.type == NodeType.CLASSROOM) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = if (node.type == NodeType.CLASSROOM) FontWeight.Medium else FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (node.type != NodeType.CLASSROOM) {
                    Spacer(modifier = Modifier.width(12.dp))
                    StatusBadge(node.type)
                }
            }
            if (node.type == NodeType.CLASSROOM) {
                Text(
                    text = "Identifiant: ${node.originalId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Actions
        val actions = actionsFor(node.type)
        Row(verticalAlignment = Alignment.CenterVertically) {
            actions.forEach { action ->
                val icon = when (action) {
                    NodeAction.ADD_CLASS -> Icons.Default.Add
                    else -> Icons.Default.Info
                }
                IconButton(
                    onClick = {
                        if (action == NodeAction.ADD_CLASS) isUpdateSchoolClassDialogOpen = true
                        onAction(node, action)
                    }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = action.name,
                        tint = if (action == NodeAction.ADD_CLASS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (node.type == NodeType.CLASSROOM) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }

    if (isUpdateSchoolClassDialogOpen) {
        UpdateSchoolClassDialog(
            schoolTreeNode = node,
            onCreateClass = { _ ->
                onAction(node, NodeAction.ADD_CLASS)
                isUpdateSchoolClassDialogOpen = false
            },
            onDismiss = { isUpdateSchoolClassDialogOpen = false }
        )
    }
}

@Composable
fun StatusBadge(type: NodeType) {
    val color = when (type) {
        NodeType.CYCLE -> MaterialTheme.colorScheme.tertiary
        NodeType.SECTION -> MaterialTheme.colorScheme.secondary
        NodeType.MAJOR -> MaterialTheme.colorScheme.primary
        NodeType.GRADE_LEVEL -> Color(0xFFF59E0B) // Warning-like color
        else -> MaterialTheme.colorScheme.outline
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Text(
            text = type.name,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

fun actionsFor(type: NodeType): List<NodeAction> {
    return when (type) {
        NodeType.CYCLE -> listOf(NodeAction.INFO_CYCLE)
        NodeType.SECTION -> listOf(NodeAction.INFO_SECTION)
        NodeType.MAJOR -> listOf(NodeAction.INFO_MAJOR)
        NodeType.GRADE_LEVEL -> listOf(NodeAction.ADD_CLASS, NodeAction.INFO_GRADE_LEVEL)
        else -> emptyList()
    }
}

@Composable
fun UpdateSchoolClassDialog(
    schoolTreeNode: SchoolTreeNode,
    onCreateClass: (CreateClassFromTemplateRequest) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        var className by remember { mutableStateOf("") }
        var classCapacity by remember { mutableStateOf("") }

        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .width(IntrinsicSize.Min)
            ) {
                Text(
                    text = "Ajouter une classe",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pour le niveau : ${schoolTreeNode.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = className,
                    onValueChange = { className = it },
                    label = { Text("Nom de la classe") },
                    shape = MaterialTheme.shapes.large,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = classCapacity,
                    onValueChange = { if (it.all { char -> char.isDigit() }) classCapacity = it },
                    label = { Text("Capacité de la classe") },
                    shape = MaterialTheme.shapes.large,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            onCreateClass(
                                CreateClassFromTemplateRequest(
                                    templateGradeLevelId = schoolTreeNode.originalId,
                                    name = className,
                                    capacity = classCapacity.toIntOrNull() ?: 0
                                )
                            )
                            onDismiss()
                        },
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
