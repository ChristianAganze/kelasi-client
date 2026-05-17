package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import com.drcmind.kelasisuite.domain.model.SchoolTreeNode

data class VisibleNode(
    val node: SchoolTreeNode,
    val depth: Int
)