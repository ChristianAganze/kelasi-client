package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.drcmind.kelasisuite.domain.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.domain.model.SchoolTreeNode
import com.drcmind.kelasisuite.domain.util.NodeType
import com.drcmind.kelasisuite.navigation.Route
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StructureScreen(
    viewModel: SchoolStructureViewModel = koinViewModel(),
    schoolStructureBackStack: NavBackStack<NavKey>
){

    val visibleNodes =
        viewModel.visibleNodes.value
    val uiState by viewModel.state.collectAsState()


    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(12.dp)) {
        StatsCardsRow(
            totalCycles = uiState.totalCycles,
            totalSections = uiState.totalSections,
            totalGradeLevels = uiState.totalGradeLevels,
            totalClasses = uiState.totalClasses
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            items(items = visibleNodes, key = { "${it.node.type}-${it.node.id}" }) { visibleNode ->
                println("Rendering TreeNodeRow for node: ${visibleNode.node.title}")
                TreeNodeRow(
                    visibleNode = visibleNode,
                    onToggle = {
                        viewModel.onToggle(it)
                    },
                    onAction = { node, action ->
                        viewModel.onAction(node, action)
                    },
                    onNavigateToClassDetails = {
                        schoolStructureBackStack.add(Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail(visibleNode.node.originalId))
                    }
                )
            }
        }
    }
}

fun actionsFor(
    type: NodeType
): List<NodeAction> {
    return when(type) {
        NodeType.SCHOOL ->
            emptyList()
        NodeType.CYCLE ->
            listOf(NodeAction.INFO_CYCLE)
        NodeType.SECTION ->
            listOf(NodeAction.INFO_SECTION)
        NodeType.MAJOR ->
            listOf(NodeAction.INFO_MAJOR)
        NodeType.GRADE_LEVEL ->
            listOf(NodeAction.ADD_CLASS, NodeAction.INFO_GRADE_LEVEL)
        NodeType.CLASSROOM ->
            emptyList()
    }
}

@Composable
fun TreeNodeRow(
    visibleNode: VisibleNode,
    onToggle: (SchoolTreeNode) -> Unit,
    onAction: (SchoolTreeNode, NodeAction) -> Unit,
    onNavigateToClassDetails : () -> Unit,
) {
    var isUpdateSchoolClassDialogOpen by remember { mutableStateOf(false) }
    val node = visibleNode.node
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = (visibleNode.depth * 20).dp,
                top = 4.dp,
                bottom = 4.dp,
                end = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                node.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
                node.type == NodeType.CLASSROOM -> {

                }
                else -> {
                    if (node.expanded)
                        IconButton(onClick = { onToggle(node) }) {
                            Icon(
                                Icons.Default.ExpandLess,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    else
                        IconButton(onClick = { onToggle(node) }) {
                            Icon(
                                Icons.Default.ExpandMore,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (node.type == NodeType.CLASSROOM) {
            ListItem(
                headlineContent = { Text(node.title) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null
                    )
                },
                overlineContent = {
                    Text("Classe")
                },
                modifier = Modifier.clickable(onClick = {
                    onNavigateToClassDetails()
                })
            )
        }else{
            Row(modifier = Modifier.weight(1f)){
                Text(
                    text = node.title + " ${node.originalId}"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = node.type.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = getBadgeColor(node.type).second,
                    modifier = Modifier
                        .background(
                            color = getBadgeColor(node.type).first, // Use the helper function here
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

        }
        val actions = actionsFor(node.type)
        if (actions.isNotEmpty()) {
            Row {
                actions.forEach { action ->
                    when (action) {
                        NodeAction.INFO_CYCLE -> {
                            IconButton(onClick = { onAction(node, action) }) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        NodeAction.INFO_SECTION -> {
                            IconButton(onClick = { onAction(node, action) }) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        NodeAction.INFO_MAJOR -> {
                            IconButton(onClick = { onAction(node, action) }) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "info major",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        NodeAction.INFO_GRADE_LEVEL -> {
                            IconButton(onClick = { onAction(node, action) }) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "info grade level",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        NodeAction.ADD_CLASS -> {
                            IconButton(onClick = {
                                isUpdateSchoolClassDialogOpen = true
                                onAction(node, action)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add class",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if(isUpdateSchoolClassDialogOpen){
                                UpdateSchoolClassDialog(
                                    schoolTreeNode = node,
                                    onCreateClass = {

                                        isUpdateSchoolClassDialogOpen = false
                                    },
                                    onDismiss = {
                                        isUpdateSchoolClassDialogOpen = false
                                    }

                                )
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun getBadgeColor(schoolNodeTpe: NodeType): Pair<Color, Color> {
    return when (schoolNodeTpe) {
        NodeType.CYCLE -> Pair(
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.onTertiary
        )

        NodeType.SECTION -> Pair(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary
        )

        NodeType.MAJOR -> Pair(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )

        NodeType.GRADE_LEVEL -> Pair(
            MaterialTheme.colorScheme.tertiaryFixed,
            MaterialTheme.colorScheme.onTertiaryFixed
        )

        else -> Pair(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatsCardsRow(
    totalCycles: Int,
    totalSections: Int,
    totalGradeLevels: Int,
    totalClasses: Int
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            StatsCard(title = "Cycles", value = totalCycles)
        }
        item {
            StatsCard(title = "Sections", value = totalSections)
        }
        item {
            StatsCard(title = "Grade Levels", value = totalGradeLevels)
        }
        item {
            StatsCard(title = "Classes", value = totalClasses)
        }
    }
}

@Composable
fun StatsCard(title: String, value: Int) {
    OutlinedCard(
        modifier = Modifier
            .width(120.dp)
            .height(80.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun UpdateSchoolClassDialog(
    schoolTreeNode: SchoolTreeNode,
    onCreateClass : (CreateClassFromTemplateRequest) -> Unit,
    onDismiss: () -> Unit
){
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(dismissOnClickOutside = false)){
        var className by remember { mutableStateOf("") }
        var classCapacity by remember { mutableStateOf("") }
        Card {
            Column(modifier = Modifier.padding(32.dp)) {
                Text(text = "Ajouter une classe", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = schoolTreeNode.title, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = className,
                    onValueChange = { str ->
                        className = str
                    },
                    placeholder = {Text("Nom de la classe")},
                    label = {Text("Nom de la classe")},
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = classCapacity,
                    onValueChange = { str ->
                        classCapacity = str
                    },
                    placeholder = {Text("Capacité de la classe")},
                    label = {Text("Capacité de la classe")},
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = {onDismiss()}){
                        Text(text = "Annuler")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onCreateClass(CreateClassFromTemplateRequest(
                            templateGradeLevelId = schoolTreeNode.originalId,
                            name = className,
                            capacity = classCapacity.toInt()
                        ))
                        onDismiss()
                    }){
                        Text(text = "Enregistrer")
                    }
                }
            }
        }
    }

}


