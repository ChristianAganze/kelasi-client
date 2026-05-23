package com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.enrollment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentDto
import com.drcmind.kelasisuite.domain.util.toDdMmYyyyWithTime
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EnrollmentScreen(
    viewModel: EnrollmentViewModel = koinViewModel(),
) {
    var showEnrollDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment.List::class,
                        Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment.List.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment.Profile::class,
                        Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment.Profile.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment.List
    )
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp,
                verticalPartitionSpacerSize = 0.dp,
                defaultPanePreferredWidth = 800.dp
            )
    }
    val listDetailsStrateggy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
        directive = directive
    )

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailsStrateggy),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment.List>(
                metadata = ListDetailSceneStrategy.listPane()
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                SearchBar(
                                    inputField = {

                                        InputField(
                                            modifier = Modifier.height(44.dp).padding(horizontal = 12.dp),
                                            query = uiState.searchQueryEnrollment,
                                            onQueryChange = viewModel::onEnrollmentSearchQueryChange,
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
                                IconButton(onClick = { showEnrollDialog = true }) {
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
                                        "SERNIE",
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
                                    Text(
                                        "ACTION",
                                        modifier = Modifier.weight(0.5f),
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
                                        items(uiState.enrollments) { enrollment ->
                                            EnrollmentRow(
                                                enrollment = enrollment,
                                                onClick = {
                                                    viewModel.selectEnrollment(enrollment)
                                                    if(backStack.last() != Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment.List){
                                                        backStack.removeLastOrNull()
                                                    }
                                                    backStack.add(Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment.Profile(enrollment.student.id))
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
            entry<Route.SchoolAdmin.Academics.StudentEnrollment.Enrollment.Profile>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {key->
                EnrollmentDetailScreen(
                    enrollment = uiState.selectedEnrollment ?: return@entry,
                    onBack = { backStack.removeLastOrNull() },
                    onEdit = {},
                    onPrintCard = {},
                    onPrintConfirmation = {},
                    onTransfer = {}
                )
            }
        }
    )

    if (showEnrollDialog) {
        EnrollmentDialog(
            onDismiss = { showEnrollDialog = false },
            onSuccess = { showEnrollDialog = false },
        )
    }
}


@Composable
fun EnrollmentRow(
    enrollment: EnrollmentDto,
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
                    Text(enrollment.student.firstName.take(1), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        enrollment.student.firstName + " " + enrollment.student.lastName,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        enrollment.enrollmentDate.toDdMmYyyyWithTime(),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Text(
                enrollment.student.id.toString() + "-SER094",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center

            )
            Text(
                enrollment.schoolClass.gradeLevel + " " + enrollment.schoolClass.name,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center

            )
            IconButton(onClick = { /*TODO*/ },modifier = Modifier.weight(0.5f)){
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
        }
    }
}
