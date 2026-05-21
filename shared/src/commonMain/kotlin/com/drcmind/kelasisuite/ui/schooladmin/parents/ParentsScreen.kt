package com.drcmind.kelasisuite.ui.schooladmin.parents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.domain.dto.ParentDto
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import com.drcmind.kelasisuite.ui.schooladmin.staff_hr.teachers.TeacherDetailsScreen
import com.drcmind.kelasisuite.ui.schooladmin.staff_hr.teachers.TeacherItem
import com.drcmind.kelasisuite.ui.schooladmin.staff_hr.teachers.TeacherRow
import com.drcmind.kelasisuite.ui.schooladmin.staff_hr.teachers.TeacherTableCard
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ParentsScreen(
    viewModel: ParentsViewModel = koinViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Parents.List::class,
                        Route.SchoolAdmin.Parents.List.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Parents.Profile::class,
                        Route.SchoolAdmin.Parents.Profile.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Parents.List
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
            entry<Route.SchoolAdmin.Parents.List>(
                metadata = ListDetailSceneStrategy.listPane()
            ) {

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    OutlinedTextField(
                                        value = uiState.searchQuery,
                                        onValueChange = viewModel::onSearchQueryChange,
                                        modifier = Modifier.fillMaxWidth().padding(16.dp).widthIn(max = 400.dp),
                                        placeholder = { Text("Rechercher un parent (Nom, ID)...", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                        trailingIcon = {
                                            if (uiState.searchQuery.isNotEmpty()) {
                                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                                    Icon(Icons.Default.Close, contentDescription = null)
                                                }
                                            }
                                        },
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.extraLarge,
                                    )
                                },
                                actions = {
                                    OutlinedIconButton(onClick = {}){
                                        Icon(Icons.Default.Add, contentDescription = "Add Parent")
                                    }
                                    OutlinedIconButton(onClick = {}){
                                        Icon(Icons.Default.MoreVert, contentDescription = "More menu")
                                    }
                                }
                            )
                        }
                    ){
                        ParentTable(
                            uiState.list, {
                                backStack.add(
                                    Route.SchoolAdmin.StaffHR.Teachers.ListDetails.Profile(
                                        it
                                    )
                                )
                            }
                        )
                    }
                }
            }
            entry<Route.SchoolAdmin.Parents.Profile>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {
                Scaffold(
                    topBar = {
                        LargeFlexibleTopAppBar(
                            title = {
                                Text("Profil du parent")
                            }
                        )
                    }
                ){
                    Column(modifier = Modifier.fillMaxSize().padding(it)) {
                        Text("Détails")
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentTable(
    parents: List<ParentDto>,
    onNavigateToParentProfile: (Long) -> Unit,
) {

    Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Noms",
                modifier = Modifier.weight(2f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                "Adresse",
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                "Etudiants liés",
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                "Actions",
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        when (parents.isEmpty()) {
            true -> Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(AppIcons.peoples, contentDescription = null, Modifier.size(100.dp))
                Spacer(Modifier.height(10.dp))
                Text("Aucun parent trouvé")
            }

            false -> parents.forEach { parent ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ParentRow(
                    parent,
                    onClick = { onNavigateToParentProfile(parent.id!! ) },
//                  onEdit = { onEditTeacher(teacher.id.toLong()) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun ParentRow(
    parent: ParentDto,
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
                    Text(parent.fullName .take(1), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        parent.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        parent.occupation.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                parent.occupation.toString(),
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1
            )

            if(parent.linkages.isEmpty()){
                Text("Non lié")
            }else{
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-12).dp)
                ){
                    parent.linkages.forEach { linkage ->
                        CircularProfile(linkage.student.fullName.take(1))
                    }
                }
            }

        }
    }
}

@Composable
fun CircularProfile(
    text: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Gray)
            .border(2.dp, Color.White, CircleShape)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}