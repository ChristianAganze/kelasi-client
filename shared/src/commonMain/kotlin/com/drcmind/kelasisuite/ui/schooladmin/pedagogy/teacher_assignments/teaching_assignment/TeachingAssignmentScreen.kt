package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teaching_assignment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import com.drcmind.kelasisuite.ui.components.EmptyDetailPlaceholder
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teachers.AddTeacherDialog
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TeachingAssignmentScreen(
    viewModel: TeachingAssignmentViewModel = koinViewModel()
){
    val uiState by viewModel.uiState.collectAsState()

    val teachingAssignmentBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.List::class,
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.List.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.Profile::class,
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.Profile.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.AddUpdate::class,
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.AddUpdate.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.List
    )

    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(
                horizontalPartitionSpacerSize = 0.dp,
                verticalPartitionSpacerSize = 0.dp,
                defaultPanePreferredWidth = 800.dp
            )
    }

    val listDetailsStrateggy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
        directive = directive
    )

    val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = teachingAssignmentBackStack,
        onBack = { teachingAssignmentBackStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailsStrateggy, dialogStrategy),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.List>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        EmptyDetailPlaceholder(
                            icon = Icons.Filled.NoFood,
                            title = "Aucune affectation sélectionnée",
                            subtitle = "Sélectionner une affectation",
                        )
                    }
                )
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                SearchBar(
                                    inputField = {

                                        InputField(
                                            modifier = Modifier.height(44.dp).padding(horizontal = 12.dp),
                                            query = uiState.searchQuery,
                                            onQueryChange = viewModel::onSearchQueryChange,
                                            onSearch = {},
                                            expanded = false,
                                            onExpandedChange = {},
                                            placeholder = {
                                                Text("Rechercher enseignant, cours ou classse")
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Search, null)
                                            }
                                        )
                                    },
                                    expanded = false,
                                    onExpandedChange = {}
                                ) {}
                            },
                            actions = {
                                IconButton(onClick = {teachingAssignmentBackStack.add(Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.AddUpdate(null))}) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Ajouter",
                                    )
                                }

                            }
                        )
                    }
                )
                { innerPadding ->
                    if (uiState.isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(innerPadding)
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                        ) {
                            Column {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "ENSEIGNANT",
                                            modifier = Modifier.weight(2f),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            "COURS",
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            "CLASSE",
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                when (uiState.teachingAssignments.isEmpty()) {
                                    true -> Column(
                                        modifier = Modifier.fillMaxSize().padding(vertical = 40.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(AppIcons.peoples, contentDescription = null, Modifier.size(100.dp))
                                        Spacer(Modifier.height(10.dp))
                                        Text("Aucun élève/étudiant trouvé")
                                    }
                                    false -> LazyColumn {
                                        items(uiState.teachingAssignments) { assignment ->
                                            TeachingAssignmentRow(
                                                assignment,
                                                onClick = {
                                                    viewModel.setActiveTeachingAssignment(assignment)
                                                    if(teachingAssignmentBackStack.last() != Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.List){
                                                        teachingAssignmentBackStack.removeLastOrNull()
                                                    }
                                                    teachingAssignmentBackStack.add(Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.Profile(assignment.id))
                                                },

                                                )
                                            HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 24.dp),
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
            entry<Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.Profile>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key->
                TeachingAssignmentDetailsScreen(
                    onBack = {
                        teachingAssignmentBackStack.removeLastOrNull()
                    }
                )
            }
            entry<Route.SchoolAdmin.Pedagogy.TeacherAssignments.TeachingAssignment.AddUpdate>(
                metadata = DialogSceneStrategy.dialog(
                    DialogProperties(dismissOnBackPress = false)
                )
            ) {key->
                AddTeacherDialog(
                    teacherId = key.teacherId,
                    onBack = {
                        teachingAssignmentBackStack.removeLast()
                    },
                    onTeacherAdded = {}

                )
            }
        }
    )
}


@Composable
fun TeachingAssignmentRow(
    teachingAssignment: TeachingAssignmentDTO,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(2f)) {
                Box(
                    Modifier.size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(teachingAssignment.teacherName.take(1), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        teachingAssignment.teacherName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        teachingAssignment.subjectCode,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                teachingAssignment.subjectName ?: "---",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                teachingAssignment.className,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

        }
    }
}