package com.drcmind.kelasisuite.domain.util

import com.drcmind.kelasisuite.domain.model.SchoolTreeNode
import com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure.VisibleNode

enum class NodeType {
    SCHOOL,
    CYCLE,
    SECTION,
    MAJOR,
    GRADE_LEVEL,
    CLASSROOM
}

fun buildVisibleNodes(
    allNodes: List<SchoolTreeNode>
): List<VisibleNode> {
    val grouped = allNodes.groupBy { it.parentId }
    val result = mutableListOf<VisibleNode>()

    fun getDesiredDepthForNodeType(nodeType: NodeType): Int {
        return when (nodeType) {
            NodeType.SCHOOL -> 0
            NodeType.CYCLE -> 1
            NodeType.SECTION -> 2
            NodeType.MAJOR -> 3
            NodeType.GRADE_LEVEL -> 4
            NodeType.CLASSROOM -> 5
        }
    }

    fun appendChildren(
        parentId: String?,
        structuralDepth: Int
    ) {
        var children = grouped[parentId].orEmpty()

        // Filter out self-referencing nodes to prevent infinite recursion
        // A node cannot be its own child. If node.id == parentId, it's a data anomaly causing cycles.
        if (parentId != null) {
            children = children.filter { it.id != parentId }
        }

        children.forEach { node ->
            val depthForVisibleNode = getDesiredDepthForNodeType(node.type)
            result.add(VisibleNode(node = node, depth = depthForVisibleNode))

            if (node.expanded) {
                appendChildren(
                    parentId = node.id,
                    structuralDepth = structuralDepth + 1
                )
            }
        }
    }
    appendChildren(
        parentId = null,
        structuralDepth = 0
    )
    return result
}
