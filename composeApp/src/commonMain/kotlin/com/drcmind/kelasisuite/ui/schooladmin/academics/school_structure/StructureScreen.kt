package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.domain.model.SchoolTreeNode
import com.drcmind.kelasisuite.domain.util.NodeType
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StructureScreen(
    viewModel: SchoolStructureViewModel = koinViewModel(),
    onAddClass: () -> Unit,
    onEditClass: (Long) -> Unit,
    onSelectClass: (Long) -> Unit
){

    val visibleNodes =
        viewModel.visibleNodes.value
    val uiState by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        StatsCardsRow(
            totalCycles = uiState.totalCycles,
            totalSections = uiState.totalSections,
            totalGradeLevels = uiState.totalGradeLevels,
            totalClasses = uiState.totalClasses
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(12.dp)) {
            items(items = visibleNodes, key = { "${it.node.type}-${it.node.id}" }) { visibleNode ->
                println("Rendering TreeNodeRow for node: ${visibleNode.node.title}")
                TreeNodeRow(
                    visibleNode = visibleNode,
                    onToggle = {
                        viewModel.onToggle(it)
                    },
                    onAction = { node, action ->
                        viewModel.onAction(node, action)
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
    onAction: (SchoolTreeNode, NodeAction) -> Unit
) {
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
                            Icon(Icons.Default.ExpandLess, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    else
                        IconButton(onClick = { onToggle(node) }) {
                            Icon(Icons.Default.ExpandMore, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (node.type == NodeType.CLASSROOM) {
            SchoolClassCard(
                schoolClassNode = node,
                onClick = {onAction(node, NodeAction.INFO_CLASS) },
                onEdit = {onAction(node, NodeAction.EDIT_CLASS) },
                onDelete = {onAction(node, NodeAction.DELETE_CLASS) }
            )
        }else{
            Row(modifier = Modifier.weight(1f)){
                Text(
                    text = node.title
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
                            IconButton(onClick = { onAction(node, action)}){
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        NodeAction.INFO_SECTION -> {
                            IconButton(onClick = { onAction(node, action) }){
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        NodeAction.INFO_MAJOR -> {
                            IconButton(onClick = { onAction(node, action) }){
                                Icon(imageVector = Icons.Default.Info, contentDescription = "info major", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        NodeAction.INFO_GRADE_LEVEL -> {
                            IconButton(onClick = { onAction(node, action) }){
                                Icon(imageVector = Icons.Default.Info, contentDescription = "info grade level", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        NodeAction.ADD_CLASS -> {
                            IconButton(onClick = { onAction(node, action) }){
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add class", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchoolClassCard(
    schoolClassNode: SchoolTreeNode,
    onClick: () -> Unit, // Action for clicking the main card or 'Détails' button
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick), // Make the entire card clickable for details
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Add a subtle shadow
        shape = RoundedCornerShape(12.dp) // Apply more rounded corners to the card
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Increased padding for better internal spacing
            // Top Row: School Icon and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically // Vertically center items in this row
            ) {
                // School Icon
                Box(
                    modifier = Modifier.size(24.dp), // Keep the box size, but tint the icon
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.school, // Assuming AppIcons.school is available
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary // Give the school icon a primary tint
                    )
                }

                // Edit and Delete Buttons
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp) // Maintain a good touch target size
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant // Use onSurfaceVariant for better contrast
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error // Error color for delete
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Reduced spacer height

            // Title and Badge (integrated here)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = schoolClassNode.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f) // Allows the title to take available space
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = schoolClassNode.type.name,
                    color = getBadgeColor(schoolClassNode.type).second,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .background(
                            color = getBadgeColor(schoolClassNode.type).first,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Slightly more space before the bottom section

            // Bottom Row: Person Icon and 'Détails' Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Person Icon (assuming it might represent student count or similar)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.person, // Assuming AppIcons.person is available
                        contentDescription = null,
                        modifier = Modifier.size(18.dp), // Slightly larger icon
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text("50", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }

                // 'Détails' Button
                Button(
                    onClick = onClick, // This button also triggers the main card's onClick action
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer, // Use a softer primary variant for the button
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp) // Adjusted padding for button
                ) {
                    Text(
                        "Détails",
                        style = MaterialTheme.typography.labelMedium, // Use labelMedium for consistent button text style
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun getBadgeColor(schoolNodeTpe: NodeType): Pair<Color,Color> {
    return when (schoolNodeTpe) {
        NodeType.CYCLE -> Pair(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
        NodeType.SECTION -> Pair(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
        NodeType.MAJOR -> Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
        NodeType.GRADE_LEVEL -> Pair(MaterialTheme.colorScheme.tertiaryFixed, MaterialTheme.colorScheme.onTertiaryFixed)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun StatsCardsRow(totalCycles : Int,
                  totalSections : Int,
                  totalGradeLevels : Int,
                  totalClasses : Int) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
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
    Card(
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


