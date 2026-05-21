package com.drcmind.kelasisuite.ui.schooladmin.staff_hr.teachers


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun TeachersListDetailScreen(
    viewModel: TeachersViewModel = koinViewModel(),
    onEditTeacher: (Long) -> Unit
) {
    val uiState by viewModel.listState.collectAsState()
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.StaffHR.Teachers.ListDetails.List::class,
                        Route.SchoolAdmin.StaffHR.Teachers.ListDetails.List.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.StaffHR.Teachers.ListDetails.Profile::class,
                        Route.SchoolAdmin.StaffHR.Teachers.ListDetails.Profile.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.StaffHR.Teachers.ListDetails.List
    )
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp, verticalPartitionSpacerSize = 0.dp)
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
            entry<Route.SchoolAdmin.StaffHR.Teachers.ListDetails.List>(
                metadata = ListDetailSceneStrategy.listPane()
            ) {

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    TeacherTableCard(
                        uiState.teachers, {
                            backStack.add(
                                Route.SchoolAdmin.StaffHR.Teachers.ListDetails.Profile(
                                    it
                                )
                            )
                        }, uiState.searchQuery, viewModel::onSearchQueryChange, onEditTeacher
                    )


                }
            }
            entry<Route.SchoolAdmin.StaffHR.Teachers.ListDetails.Profile>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {
                TeacherDetailsScreen(
                    teacherId = it.teacherId,
                    onBack = {
                        if (backStack.size > 1) {
                            backStack.removeLast()
                        }
                    },
                    onEdit = { id ->
                        onEditTeacher(id)
                    }
                )
            }
        }
    )


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherTableCard(
    teachers: List<TeacherItem>,
    onNavigateToTeacherDetail: (Long) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onEditTeacher: (Long) -> Unit
) {

    Column(
        Modifier.clip(
            MaterialTheme.shapes.extraLarge
        ).background(MaterialTheme.colorScheme.surface).border(
            shape = MaterialTheme.shapes.extraLarge,
            border =
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        )
    ) {


        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            placeholder = { Text("Rechercher un enseignant (Nom, ID Paie, Qualifications)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            singleLine = true,
            // Configuration pour supprimer la ligne et le contour
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,    // Supprime la ligne sous le champ (focus)
                unfocusedIndicatorColor = Color.Transparent,  // Supprime la ligne sous le champ
                disabledIndicatorColor = Color.Transparent,
            )
        )
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
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        when (teachers.isEmpty()) {
            true -> Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(AppIcons.peoples, contentDescription = null, Modifier.size(100.dp))
                Spacer(Modifier.height(10.dp))
                Text("Aucun enseignant trouvé")
            }

            false -> teachers.forEach { teacher ->
                TeacherRow(
                    teacher,
                    onClick = { onNavigateToTeacherDetail(teacher.id.toLong()) },
//                        onEdit = { onEditTeacher(teacher.id.toLong()) }
                )
                if (teachers.last() != teacher) HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun TeacherRow(
    teacher: TeacherItem,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
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