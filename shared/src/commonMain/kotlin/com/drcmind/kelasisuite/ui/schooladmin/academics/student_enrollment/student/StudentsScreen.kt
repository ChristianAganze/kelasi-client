package com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import com.drcmind.kelasisuite.ui.components.EmptyDetailPlaceholder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun StudentsScreen(
    viewModel: StudentsViewModel = koinViewModel()
) {
    val uiState by viewModel.listState.collectAsState()
    var isUpdateStudentDialogOpen by remember { mutableStateOf(false) }

        val backStack = rememberNavBackStack(
            configuration = SavedStateConfiguration {
                serializersModule = SerializersModule {
                    polymorphic(NavKey::class) {
                        subclass(
                            Route.SchoolAdmin.Academics.StudentEnrollment.Students.List::class,
                            Route.SchoolAdmin.Academics.StudentEnrollment.Students.List.serializer()
                        )
                        subclass(
                            Route.SchoolAdmin.Academics.StudentEnrollment.Students.Profile::class,
                            Route.SchoolAdmin.Academics.StudentEnrollment.Students.Profile.serializer()
                        )
                    }
                }
            },
            Route.SchoolAdmin.Academics.StudentEnrollment.Students.List
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
            directive = directive,
            adaptStrategies = ListDetailPaneScaffoldDefaults.adaptStrategies()
        )

        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            sceneStrategies = listOf(listDetailsStrateggy),
            entryProvider = entryProvider {
                entry<Route.SchoolAdmin.Academics.StudentEnrollment.Students.List>(
                    metadata = ListDetailSceneStrategy.listPane(

                        detailPlaceholder = {
                            EmptyDetailPlaceholder(
                                icon = Icons.Filled.NoFood,
                                title = "Aucun élève selectionnée",
                                subtitle = "Sélectionner un élève",
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
                                    IconButton(onClick = { isUpdateStudentDialogOpen = true }) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                    }
                                    IconButton(onClick = {}){
                                        Icon(Icons.Default.MoreVert, contentDescription = null)
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
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
                                        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
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
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    when (uiState.students.isEmpty()) {
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
                                            items(uiState.students) { student ->
                                                StudentRow(
                                                    student = student,
                                                    onClick = {
                                                        viewModel.setActiveStudent(student)
                                                        if(backStack.last() != Route.SchoolAdmin.Academics.StudentEnrollment.Students.List){
                                                            backStack.removeLastOrNull()
                                                        }
                                                        backStack.add(Route.SchoolAdmin.Academics.StudentEnrollment.Students.Profile(student.id))
                                                    }
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
                entry<Route.SchoolAdmin.Academics.StudentEnrollment.Students.Profile>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {key->
                    StudentDetailScreen(
                        student = uiState.activeStudent,
                        onBack = { backStack.removeLastOrNull() },
                        onEdit = {},
                        isLoading = uiState.isLoading,
                        error = uiState.error
                    )
                }
            }
        )

        if(isUpdateStudentDialogOpen){
            AddStudentDialog(
                studentId = uiState.activeStudent?.id,
                onDismiss = {isUpdateStudentDialogOpen = false},
                onStudentAdded = {},
                viewModel = viewModel
            )
        }

}

@Composable
fun StudentRow(
    student: StudentDTO,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.clickable { onClick() }

    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(2f)) {
                Box(
                    Modifier.size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(student.fullName.take(1), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        student.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        student.dateOfBirth.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Text(
                student.sernieNumber.toString(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentDialog(
    studentId: Long? = null,
    onDismiss: () -> Unit,
    onStudentAdded: () -> Unit,
    viewModel: StudentsViewModel = koinViewModel()
) {

    val state by viewModel.formState.collectAsState()

    LaunchedEffect(studentId) {
        if (studentId != null) {
            viewModel.prepareFormForEdit(studentId)
        } else {
            viewModel.resetForm()
        }
    }

    if (state.isSuccess) {
        onStudentAdded()
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(
                            text = if (studentId == null)
                                "Nouvel élève"
                            else
                                "Modifier élève",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Informations essentielles",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null)
                    }
                }
                Spacer(Modifier.height(20.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = state.lastName,
                            onValueChange = viewModel::onLastNameChange,
                            label = { Text("Nom") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )

                        OutlinedTextField(
                            value = state.firstName,
                            onValueChange = viewModel::onFirstNameChange,
                            label = { Text("Prénom") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedTextField(
                            value = state.dateOfBirth,
                            onValueChange = viewModel::onDateOfBirthChange,
                            label = { Text("Naissance") },
                            placeholder = { Text("2010-05-10") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )

                        OutlinedTextField(
                            value = state.religion,
                            onValueChange = viewModel::onReligionChange,
                            label = { Text("Religion") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    OutlinedTextField(
                        value = state.previousSchool,
                        onValueChange = viewModel::onPreviousSchoolChange,
                        label = { Text("École précédente") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.address,
                        onValueChange = viewModel::onAddressChange,
                        label = { Text("Adresse") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {
                            viewModel.saveStudent(studentId)
                        },
                        enabled = !state.isLoading,
                        modifier = Modifier.height(42.dp)
                    ) {

                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                if (studentId == null)
                                    "Ajouter"
                                else
                                    "Mettre à jour"
                            )
                        }
                    }
                }
            }
        }
    }
}
