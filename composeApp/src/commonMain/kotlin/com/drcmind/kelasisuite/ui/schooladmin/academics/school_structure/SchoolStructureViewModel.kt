package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.domain.dto.GradeLevelDTO
import com.drcmind.kelasisuite.domain.dto.MajorDto
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.domain.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.domain.dto.SectionDTO
import com.drcmind.kelasisuite.domain.model.SchoolTreeNode
import com.drcmind.kelasisuite.domain.util.NodeType
import com.drcmind.kelasisuite.domain.util.Resource
import com.drcmind.kelasisuite.domain.util.buildVisibleNodes
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SchoolStructureViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel()
{
    private val _state = MutableStateFlow(ClassesState())
    val state: StateFlow<ClassesState> = _state.asStateFlow()

    var nodes = mutableStateListOf<SchoolTreeNode>()

    init {
         loadRoots()
    }

    val visibleNodes: State<List<VisibleNode>> = derivedStateOf {
        println("derivedStateOf: Re-evaluating visibleNodes. Current 'nodes' size: ${nodes.size}. Content: $nodes")
        val builtNodes = buildVisibleNodes(nodes)
        println("buildVisibleNodes returned ${builtNodes.size} nodes.")
        builtNodes
    }

    private fun loadRoots() {
        println("loadRoots: Initial nodes state before loading roots: $nodes")
        schoolRepository.getSchoolSections().onEach { schoolSection ->
            when (schoolSection) {

                is Resource.Success -> {
                    val data = schoolSection.data
                    if (data != null) {
                        println("SUCCESS: Received ${data.size} school sections. Data: $data")
                        val newRootNodes = data.map { it.toSchoolTreeNode() }
                        // MODIFIED: Use composite key for existing nodes
                        val existingNodeKeys = nodes.map { "${it.id}-${it.type}" }.toSet()
                        val nodesToAdd = newRootNodes.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                        if (nodesToAdd.isNotEmpty()) {
                            nodes.addAll(nodesToAdd)
                            println("Nodes list size after addAll: ${nodes.size}")
                            println("Nodes added by loadRoots: $nodesToAdd")
                            nodesToAdd.forEach {
                                println("loadRoots: Added node ${it.title} (id: ${it.id}) childrenLoaded: ${it.childrenLoaded}")
                            }
                        }
                    } else {
                        println("SUCCESS: Received null data for school sections.")
                    }
                }
                is Resource.Error -> {
                    println("error ${schoolSection.message}")
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun onToggle(
        node: SchoolTreeNode
    ) {
        println("onToggle: Initial node state received: $node")
        println("onToggle called for: ${node.title}, id: ${node.id}, expanded: ${node.expanded}")
        if (node.type == NodeType.CLASSROOM) {
            return
        }
        if (node.expanded) {
            collapse(node.originalId, node.type) // MODIFIED: Pass node.type
        } else {
            expand(node)
        }
    }

    private fun collapse(nodeId: Long, nodeType: NodeType) { // MODIFIED: Accept nodeType
        updateNode(nodeId, nodeType) { // MODIFIED: Pass nodeType
            it.copy(expanded = false)
        }
        println("Node with id $nodeId and type $nodeType collapsed.")
    }


    private fun expand(node: SchoolTreeNode) {
        viewModelScope.launch {
            println("Expanding node: ${node.title}, id: ${node.id}, current expanded: ${node.expanded}, loading: ${node.loading}, childrenLoaded: ${node.childrenLoaded}")
            updateNode(node.originalId, node.type) { // MODIFIED: Pass node.type
                it.copy(loading = true)
            }
            println("Node ${node.title} (id: ${node.id}) set to loading=true")

            if (!node.childrenLoaded) {
                when (node.type) {
                    NodeType.CYCLE -> {
                        schoolRepository.getSectionBySchoolSectionAndSchool(node.originalId).onEach { section ->
                            when (section) {
                                is Resource.Loading -> {
                                    println("LOADING children for ${node.title} (type: ${node.type})")
                                }
                                is Resource.Success -> {
                                    println("SUCCESS fetching children for ${node.title} (type: ${node.type}). Received ${section.data?.size ?: 0} items.")
                                    val newChildren = section.data?.map { it.toSchoolTreeNode() } ?: emptyList()
                                    println("expand (CYCLE): newChildren for ${node.title}: $newChildren")
                                    // MODIFIED: Use composite key for existing nodes
                                    val existingNodeKeys = nodes.map { "${it.id}-${it.type}" }.toSet()
                                    println("expand (CYCLE): existingNodeKeys: $existingNodeKeys") // Log the new set
                                    val childrenToAdd = newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                    println("expand (CYCLE): childrenToAdd for ${node.title}: $childrenToAdd")
                                    if (childrenToAdd.isNotEmpty()) {
                                        nodes.addAll(childrenToAdd)
                                        println("Children added for ${node.title}. Total nodes size: ${nodes.size}")
                                    }
                                    updateNode(node.originalId, node.type) {
                                        it.copy(
                                            expanded = true,
                                            loading = false,
                                            childrenLoaded = true,
                                        )
                                    }
                                    println("Node ${node.title} (id: ${node.id}) set to expanded=true, loading=false, childrenLoaded=true")
                                }
                                is Resource.Error -> {
                                    updateNode(node.originalId, node.type) {
                                        it.copy(loading = false)
                                    }
                                    println("ERROR fetching children for ${node.title}. Message: ${section.message}. Set loading=false.")
                                }
                                else -> {}
                            }
                        }.launchIn(viewModelScope)
                    }
                    NodeType.SECTION -> {
                        schoolRepository.getOfferedMajorsForSchoolAndSection( node.originalId).onEach { major ->
                            when (major) {
                                is Resource.Loading -> {
                                    println("LOADING children for ${node.title} (type: ${node.type})")
                                }
                                is Resource.Success -> {
                                    println("SUCCESS fetching children for ${node.title} (type: ${node.type}). Received ${major.data?.size ?: 0} items.")
                                    val newChildren = major.data?.map { it.toSchoolTreeNode() } ?: emptyList()
                                    println("expand (SECTION): newChildren for ${node.title}: $newChildren")
                                    // MODIFIED: Use composite key for existing nodes
                                    val existingNodeKeys = nodes.map { "${it.id}-${it.type}" }.toSet()
                                    println("expand (SECTION): existingNodeKeys: $existingNodeKeys") // Log the new set
                                    val childrenToAdd = newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                    println("expand (SECTION): childrenToAdd for ${node.title}: $childrenToAdd")
                                    if (childrenToAdd.isNotEmpty()) {
                                        nodes.addAll(childrenToAdd)
                                        println("Children added for ${node.title}. Total nodes size: ${nodes.size}")
                                    }
                                    updateNode(node.originalId, node.type) {
                                        it.copy(
                                            expanded = true,
                                            loading = false,
                                            childrenLoaded = true
                                        )
                                    }
                                    println("Node ${node.title} (id: ${node.id}) set to expanded=true, loading=false, childrenLoaded=true")
                                }
                                is Resource.Error -> {
                                    updateNode(node.originalId, node.type) {
                                        it.copy(loading = false)
                                    }
                                    println("ERROR fetching children for ${node.title}. Message: ${major.message}. Set loading=false.")
                                }
                                else -> {}
                            }
                        }.launchIn(viewModelScope)
                    }
                    NodeType.MAJOR -> {
                        schoolRepository.getGradeLevelsBySchoolAndByMajor( node.originalId).onEach { gradeLevel ->
                            when (gradeLevel) {
                                is Resource.Loading -> {
                                    println("LOADING children for ${node.title} (type: ${node.type})")
                                }
                                is Resource.Success -> {
                                    println("SUCCESS fetching children for ${node.title} (type: ${node.type}). Received ${gradeLevel.data?.size ?: 0} items.")
                                    val newChildren = gradeLevel.data?.map { it.toSchoolTreeNode() } ?: emptyList()
                                    println("expand (MAJOR): newChildren for ${node.title}: $newChildren")
                                    // MODIFIED: Use composite key for existing nodes
                                    val existingNodeKeys = nodes.map { "${it.id}-${it.type}" }.toSet()
                                    println("expand (MAJOR): existingNodeKeys: $existingNodeKeys") // Log the new set
                                    val childrenToAdd = newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                    println("expand (MAJOR): childrenToAdd for ${node.title}: $childrenToAdd")
                                    if (childrenToAdd.isNotEmpty()) {
                                        nodes.addAll(childrenToAdd)
                                        println("Children added for ${node.title}. Total nodes size: ${nodes.size}")
                                    }
                                    updateNode(node.originalId, node.type) {
                                        it.copy(
                                            expanded = true,
                                            loading = false,
                                            childrenLoaded = true
                                        )
                                    }
                                    println("Node ${node.title} (id: ${node.id}) set to expanded=true, loading=false, childrenLoaded=true")
                                }
                                is Resource.Error -> {
                                    updateNode(node.originalId, node.type) {
                                        it.copy(loading = false)
                                    }
                                    println("ERROR fetching children for ${node.title}. Message: ${gradeLevel.message}. Set loading=false.")
                                }
                                else -> {}
                            }

                        }.launchIn(viewModelScope)
                    }
                    NodeType.GRADE_LEVEL -> {
                        schoolRepository.getClassesBySchoolAndGradeLevel( node.originalId).onEach { schoolClass ->
                            when (schoolClass) {
                                is Resource.Loading -> {
                                    println("LOADING children for ${node.title} (type: ${node.type})")
                                }
                                is Resource.Success -> {
                                    println("SUCCESS fetching children for ${node.title} (type: ${node.type}). Received ${schoolClass.data?.size ?: 0} items.")
                                    val newChildren = schoolClass.data?.map { it.toSchoolTreeNode() } ?: emptyList()
                                    println("expand (GRADE_LEVEL): newChildren for ${node.title}: $newChildren")
                                    // MODIFIED: Use composite key for existing nodes
                                    val existingNodeKeys = nodes.map { "${it.id}-${it.type}" }.toSet()
                                    println("expand (GRADE_LEVEL): existingNodeKeys: $existingNodeKeys") // Log the new set
                                    val childrenToAdd = newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                    println("expand (GRADE_LEVEL): childrenToAdd for ${node.title}: $childrenToAdd")
                                    if (childrenToAdd.isNotEmpty()) {
                                        nodes.addAll(childrenToAdd)
                                        println("Children added for ${node.title}. Total nodes size: ${nodes.size}")
                                    }
                                    updateNode(node.originalId, node.type) {
                                        it.copy(
                                            expanded = true,
                                            loading = false,
                                            childrenLoaded = true
                                        )
                                    }
                                    println("Node ${node.title} (id: ${node.id}) set to expanded=true, loading=false, childrenLoaded=true")
                                }
                                is Resource.Error -> {
                                    updateNode(node.originalId, node.type) {
                                        it.copy(loading = false)
                                    }
                                    println("ERROR fetching children for ${node.title}. Message: ${schoolClass.message}. Set loading=false.")
                                }

                                else -> {}
                            }

                        }.launchIn(viewModelScope)
                    }

                    else -> {
                        // If no children are expected or loaded, set loading to false and expanded to true
                        updateNode(node.originalId, node.type) {
                            it.copy(
                                expanded = true,
                                loading = false,
                                childrenLoaded = true
                            )
                        }
                        println("Node ${node.title} (id: ${node.id}) type is ${node.type}, no children to load. Set expanded=true, loading=false, childrenLoaded=true")
                    }
                }
            } else {
                // If children are already loaded, just expand the node and set loading to false
                updateNode(node.originalId, node.type) {
                    it.copy(expanded = true, loading = false)
                }
                println("Node ${node.title} (id: ${node.id}) children already loaded. Set expanded=true, loading=false.")
            }
        }
    }

    private fun updateNode(
        nodeId: Long,
        nodeType: NodeType, // MODIFIED: Accept nodeType
        transform: (SchoolTreeNode) -> SchoolTreeNode
    ) {
        // MODIFIED: Find node using both ID and Type
        val index = nodes.indexOfFirst { it.originalId == nodeId && it.type == nodeType }
        if (index == -1) {
            println("WARN: updateNode failed. Node with id $nodeId and type $nodeType not found.")
            return
        }
        val oldNode = nodes[index]
        val newNode = transform(oldNode)
        println("updateNode: Applying transform for node $nodeId (type: $nodeType). Old: $oldNode, New: $newNode")
        nodes[index] = newNode
        println("Node with id $nodeId (type: $nodeType) updated. Old: $oldNode, New: $newNode")
    }

    fun onAction(
        node: SchoolTreeNode,
        action: NodeAction
    ) {

        when (action) {
            NodeAction.ADD_CLASS -> {
                println("Add class to ${node.title}")
            }
            NodeAction.DELETE_CLASS -> {
                println("Delete class from ${node.title}")
            }
            NodeAction.INFO_CYCLE -> TODO()
            NodeAction.INFO_SECTION -> TODO()
            NodeAction.INFO_MAJOR -> TODO()
            NodeAction.INFO_GRADE_LEVEL -> TODO()
            NodeAction.INFO_CLASS -> TODO()
            NodeAction.EDIT_CLASS -> TODO()
        }
    }
}
data class ClassesState(
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val totalCycles : Int = 2,
    val totalSections : Int = 2,
    val totalGradeLevels : Int = 8,
    val totalClasses : Int = 12,
)

fun SchoolSectionDTO.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.CYCLE,
    parentId = null,
    expanded = false,
    loading = false, // Explicitly set
    childrenLoaded = false // Explicitly set
)

fun SectionDTO.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.SECTION,
    parentId = "${NodeType.CYCLE}-${this.schoolSectionId}",
)

fun MajorDto.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.MAJOR,
    parentId = "${NodeType.SECTION}-${this.sectionId}",
)

fun GradeLevelDTO.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.GRADE_LEVEL,
    parentId = "${NodeType.MAJOR}-${this.majorId}",
)

fun SchoolClassDTO.toSchoolTreeNode() : SchoolTreeNode = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.CLASSROOM,
    parentId = "${NodeType.GRADE_LEVEL}-${this.gradeLevelId}"
)
