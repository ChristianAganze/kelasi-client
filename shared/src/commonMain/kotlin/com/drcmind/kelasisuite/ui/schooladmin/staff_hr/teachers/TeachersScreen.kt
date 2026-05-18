package com.drcmind.kelasisuite.ui.schooladmin.staff_hr.teachers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
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
                        Route.SchoolAdmin.StaffHR.Teachers.ListDetails::class,
                        Route.SchoolAdmin.StaffHR.Teachers.ListDetails.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.StaffHR.Teachers.ProfileDetails::class,
                        Route.SchoolAdmin.StaffHR.Teachers.ProfileDetails.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.StaffHR.Teachers.AddUpdate::class,
                        Route.SchoolAdmin.StaffHR.Teachers.AddUpdate.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.StaffHR.Teachers.ListDetails
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme
                        .onSurface,
                    actionIconContentColor = Color.Transparent,
                ),

                title = {
                    Column {
                        Text(
                            text = "Gestion des Enseignants",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight =
                                    FontWeight.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Gérez la base de données académique et les profils de vos enseignants.",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::loadTeachers) {
                        Icon(
                            AppIcons.refresh,
                            contentDescription = "Actualiser",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(),
                        onClick = {
                            teachersBackStack.add(
                                Route.SchoolAdmin.StaffHR.Teachers.AddUpdate(null)
                            )
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter un enseignant")
                    }
                }
            )
        },
        containerColor = Color.Transparent,
    ) { padding ->
        TeachersNavigation(
            modifier = Modifier.padding(padding),
            teachersBackStack = teachersBackStack
        )
    }
}

@Composable
fun StatsGrid(state: TeachersUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Total Enseignants", state.totalTeachers.toString(), "+2.1%", Modifier.weight(1f))

    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    trend: String,
    modifier: Modifier = Modifier,
    isWarning: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                trend,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}
