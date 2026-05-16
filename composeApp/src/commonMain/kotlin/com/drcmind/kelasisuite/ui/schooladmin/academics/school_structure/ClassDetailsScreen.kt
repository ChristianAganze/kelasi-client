package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import com.drcmind.kelasisuite.ui.schooladmin.academics.calendar_periods.EvaluationPeriodRow
import com.drcmind.kelasisuite.ui.schooladmin.academics.calendar_periods.EvaluationSectionHeader
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ClassDetailsScreen(
    classId: Long,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "3e année Scientifique B")

                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More menu")
                    }
                }
            )
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            val backStack = rememberNavBackStack(
                configuration = SavedStateConfiguration {
                    serializersModule = SerializersModule {
                        polymorphic(NavKey::class) {
                            subclass(Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Main::class, Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Main.serializer())
                            subclass(Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Supporting::class, Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Supporting.serializer())
                        }
                    }
                },Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Supporting,Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Main
            )
            val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
            val directive = remember(windowAdaptiveInfo) {
                calculatePaneScaffoldDirective(windowAdaptiveInfo)
                    .copy(horizontalPartitionSpacerSize = 0.dp, verticalPartitionSpacerSize = 0.dp)
            }
            val supportingPaneStrategy = rememberSupportingPaneSceneStrategy<NavKey>(
                backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
                directive = directive
            )

            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                sceneStrategies = listOf(supportingPaneStrategy),
                entryProvider = entryProvider {
                    entry<Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Main>(
                        metadata = SupportingPaneSceneStrategy.mainPane()
                    )
                    {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            var selectedDestination by rememberSaveable {
                                mutableStateOf(SchoolClassDetailsScreenTabs.StudentList.ordinal)
                            }
                            SecondaryScrollableTabRow(
                                selectedTabIndex = selectedDestination
                            ){
                                SchoolClassDetailsScreenTabs.entries.forEachIndexed { index, destination ->
                                    Tab(
                                        selected = selectedDestination == index,
                                        onClick = {
                                            selectedDestination = index
                                        },
                                        text = {
                                            Text(
                                                text = destination.name,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            when (selectedDestination) {
                                0 -> {
                                    StudentsList()
                                }

                                1 -> {

                                }

                                2 -> {

                                }
                            }
                        }
                    }
                    entry<Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Supporting>(
                        metadata = SupportingPaneSceneStrategy.supportingPane()
                    ) {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),

                        ) {
                            Text(
                                text = "VUE D'ENSEMBLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                DailyPlanningCard()
                                TeacherMiniCard()
                            }
                        }
                    }
                }
            )

        }

    }
}

@Composable
fun StudentsList(){
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "3 ÉLÈVES INSCRITS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Text(
                text = "VOIR TOUT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LazyColumn {
            items(sampleStudents) { student ->
                StudentRowItem(student)
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.surfaceVariant)
            }
        }

    }
}


@Composable
fun StudentRowItem(student: Student) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    student.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            supportingContent = {
                Text("Student ID: ${student.id}")
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        AppIcons.person,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingContent = {
                val badgeColor = if (student.isActive) Color(0xFFE1FBEF) else MaterialTheme.colorScheme.errorContainer
                val contentColor = if (student.isActive) Color(0xFF0E6245) else MaterialTheme.colorScheme.onErrorContainer
                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = if (student.isActive) "ACTIVE" else "PROBATION",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = contentColor
                    )
                }
            }
        )
    }
}

@Composable
fun DailyPlanningCard() {
    Card(
        modifier = Modifier.padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Icon(AppIcons.curriculum, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Planning", color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("08:00 - Chimie", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 11.sp)
            Text("09:00 - Géo", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 11.sp)
            Text("10:00 - Histoire", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 11.sp)
            Text("11:00 - Education à la vie", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 11.sp)
            Text("12:00 - Math", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 11.sp)
            Text("13:00 - Math", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 11.sp)
        }

    }
}

@Composable
fun TeacherMiniCard() {
    Card(
        modifier = Modifier.padding(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Icon(AppIcons.person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Prof. Jean", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Tutilaire de la classe", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }

    }
}

data class Student(val id: String, val name: String, val isActive: Boolean)

val sampleStudents = listOf(
    Student("#2024-001", "Alice Mbuyi", true),
    Student("#2024-002", "Marc Kalonji", false),
    Student("#2024-003", "Sophie Bakala", true)
)