package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teaching_assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Grading
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.ui.schooladmin.component.InfoChip
import com.drcmind.kelasisuite.ui.schooladmin.component.SectionCard
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.component.TeacherHeaderSection
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.component.TeacherProfileTopBar
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teachers.ActivityType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeachingAssignmentDetailsScreen(
    viewModel: TeachingAssignmentViewModel = koinViewModel(),
    onBack: () -> Unit,
){
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TeacherProfileTopBar(
                teacherName = uiState.activeTeachinggAssignment?.teacherName?:"-",
                onBack = onBack
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                TeacherHeaderSection(
                    teacherName = uiState.activeTeachinggAssignment?.teacherName?:"-",
                    specialization = "",
                    grade = "",
                    numberOfClasses = 1,
                    experienceYears = 0,
                    totalHoursPerWeek = 0
                )
            }
            item {

                CurriculumAlignment(0.67f)
            }
            item {
                RecentActivitiesSection(
                    activities = uiState.activites
                )
            }
        }
    }




}


@Composable
fun CurriculumAlignment(
    currentProgress : Float,
){
    SectionCard(
        title = "Alignement selon le programme national",
        icon = Icons.Filled.Check,
    ){
        Column {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    "Conformité au programme national",
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    "${(currentProgress * 100).toInt()}%"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(
                    icon = Icons.Default.Done,
                    label = "18 Modules completés"
                )

                InfoChip(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    label = "3 Modules restants"
                )
            }

        }
    }
}


@Composable
private fun RecentActivitiesSection(
    activities: List<TeachingAssignmentActivityUI>
) {

    SectionCard(
        title = "Activités récentes",
        icon = Icons.Default.History
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            activities.forEach { activity ->

                ListItem(
                    headlineContent = {
                        Text(activity.title)
                    },

                    supportingContent = {

                        Column {

                            Text(
                                activity.description
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                activity.timestamp,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },

                    leadingContent = {

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {

                            Icon(
                                imageVector = when (activity.type) {

                                    ActivityType.LESSON_PREPARATION ->
                                        Icons.Default.Description

                                    ActivityType.CLASS_LOG ->
                                        Icons.AutoMirrored.Filled.EventNote

                                    ActivityType.GRADING ->
                                        Icons.AutoMirrored.Filled.Grading

                                    ActivityType.ATTENDANCE ->
                                        Icons.AutoMirrored.Filled.FactCheck
                                },
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}