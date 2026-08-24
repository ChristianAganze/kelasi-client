package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teachers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.graphics.Color
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
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import com.drcmind.kelasisuite.ui.components.EmptyDetailPlaceholder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun TeachersScreen(
    viewModel: TeachersViewModel = koinViewModel()
) {
    val uiState by viewModel.listState.collectAsState()

    val teachersBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.List::class,
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.List.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.Profile::class,
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.Profile.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.AddUpdate::class,
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.AddUpdate.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.List
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

        val listDetailsStrateggy = rememberListDetailSceneStrategy<NavKey>(
            backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
            directive = directive
        )

        val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }

        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = teachersBackStack,
            onBack = { teachersBackStack.removeLastOrNull() },
            sceneStrategies = listOf(listDetailsStrateggy, dialogStrategy),
            entryProvider = entryProvider {
                entry<Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.List>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            EmptyDetailPlaceholder(
                                icon = Icons.Filled.NoFood,
                                title = "Aucun enseignant selectionné",
                                subtitle = "Sélectionner un enseignant",
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
                                                    Text("Rechercher un élève (Nom, Matricule)...")
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
                                    IconButton(onClick = {teachersBackStack.add(Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.AddUpdate(null))}) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Actualiser",
                                        )
                                    }
                                    IconButton(onClick = viewModel::loadTeachers) {
                                        Icon(
                                            Icons.Default.MoreVert,
                                            contentDescription = "Actualiser",
                                            tint = MaterialTheme.colorScheme.primary
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
                                shape = MaterialTheme.shapes.medium.copy(bottomEnd = CornerSize(0.dp), bottomStart = CornerSize(0.dp))
                            ) {
                                Column {
                                    Row(
                                        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "NOMS",
                                                modifier = Modifier.weight(2f),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                            Text(
                                                "MATRICULE",
                                                modifier = Modifier.weight(1f),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                "ADRESSE",
                                                modifier = Modifier.weight(1f),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                "STATUT",
                                                modifier = Modifier.weight(1f),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    when (uiState.teachers.isEmpty()) {
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
                                            items(uiState.teachers) { teacher ->
                                                TeacherRow(
                                                    teacher,
                                                    onClick = {
                                                        if(teachersBackStack.last() != Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.List){
                                                            teachersBackStack.removeLastOrNull()
                                                        }
                                                        teachersBackStack.add(Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.Profile(teacher.id))
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
                entry<Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.Profile>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { key->
                    TeacherProfileScreen(
                        onBack = {
                            teachersBackStack.removeLastOrNull()
                        }
                    )
                    /*TeacherDetailsScreen(
                        teacherId = key.teacherId,
                        onBack = {
                            if (teachersBackStack.size > 1) {
                                teachersBackStack.removeLast()
                            }
                        },
                        onEdit = { id ->
                            teachersBackStack.add(Route.SchoolAdmin.Pedagogy.Teachers.AddUpdate(id))
                        }
                    )*/
                }
                entry<Route.SchoolAdmin.Pedagogy.TeacherAssignments.Teachers.AddUpdate>(
                    metadata = DialogSceneStrategy.dialog(
                        DialogProperties(dismissOnBackPress = false)
                    )
                ) {key->
                    AddTeacherDialog(
                        teacherId = key.teacherId,
                        onBack = {
                            teachersBackStack.removeLast()
                        },
                        onTeacherAdded = {}
                    )
                }
            }
        )
}



@Composable
fun TeacherRow(
    teacher: TeacherProfileDTO,
    onClick: () -> Unit,
) {
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
                    Text(teacher.fullName.take(1), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        teacher.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        teacher.qualifications,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                teacher.payrollId ?: "---",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                "${teacher.address.cityTerritory}, ${teacher.address.province}",
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    "ACTIF",
                    color = Color(0xFF10B981),
                    style = MaterialTheme.typography.labelMedium
                )
            }

        }
    }
}
