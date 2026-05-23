package com.drcmind.kelasisuite.domain.model

import com.drcmind.kelasisuite.domain.util.NodeType

data class SchoolTreeNode(
    val originalId: Long,
    val title: String,
    val type: NodeType,
    val parentId: String?,
    val parentTitle : String?,
    val expanded: Boolean = false,
    val loading: Boolean = false,
    val childrenLoaded: Boolean = false
) {
    val id: String
        get() = "${type}-${originalId}"
}
