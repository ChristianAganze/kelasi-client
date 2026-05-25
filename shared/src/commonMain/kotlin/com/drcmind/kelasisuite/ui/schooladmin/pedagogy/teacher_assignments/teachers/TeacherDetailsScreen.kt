@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teachers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.ui.schooladmin.component.SectionCard
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.component.TeacherHeaderSection
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.component.TeacherProfileTopBar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.Padding
import org.koin.compose.viewmodel.koinViewModel

data class TeacherProfileUiState(
    val teacherName: String = "",
    val matricule: String = "",
    val photoUrl: String? = null,
    val specialization: String = "",
    val grade: String = "",
    val experienceYears: Int = 0,
    val classes: List<TeachingClassUi> = emptyList(),
    val curriculumProgress: List<CurriculumProgressUi> = emptyList(),
    val activities: List<TeacherActivityUi> = emptyList()
)

data class TeachingClassUi(
    val className: String,
    val studentsCount: Int,
    val course: String,
    val hoursPerWeek: Int
)

data class CurriculumProgressUi(
    val className: String,
    val currentProgress: Float,
    val nationalProgress: Float
)

data class TeacherActivityUi(
    val title: String,
    val description: String,
    val timestamp: String,
    val type: ActivityType
)

enum class ActivityType {
    LESSON_PREPARATION,
    CLASS_LOG,
    GRADING,
    ATTENDANCE
}

val schedulingLabel = listOf(
    "7h00-8h45",
    "8h46-9h30",
    "9h31-10h15",
    "10h16-10h30",
    "10h36-11h15",
    "11h16-12h00",
    "12h01-12h45",
)

@Composable
fun TeacherProfileScreen(
    viewModel: TeachersViewModel = koinViewModel(),
    onBack: () -> Unit
) {

    val state = viewModel.sampleTeacherProfile

    Scaffold(
        topBar = {
            TeacherProfileTopBar(
                teacherName = state.teacherName,
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
                    teacherName = state.teacherName,
                    specialization = state.specialization,
                    grade = state.grade,
                    numberOfClasses = state.classes.count(),
                    experienceYears = state.experienceYears,
                    totalHoursPerWeek = state.classes.sumOf { it.hoursPerWeek },
                )
            }
            item {

                AcademicLoadSection(
                    classes = state.classes
                )
            }
            item {
                CurriculumProgressSection(
                    progress = state.curriculumProgress
                )
            }
            item {
                TeachingScheduleSection()
            }
            item {
                RecentActivitiesSection(
                    activities = state.activities
                )
            }
        }
    }
}


@Composable
private fun AcademicLoadSection(
    classes: List<TeachingClassUi>
) {

    SectionCard(
        title = "Charge académique",
        icon = Icons.AutoMirrored.Filled.MenuBook
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            classes.forEach { schoolClass ->

                ElevatedCard {

                    ListItem(
                        headlineContent = {
                            Text(schoolClass.className)
                        },

                        supportingContent = {

                            Column {

                                Text(
                                    schoolClass.course
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    "${schoolClass.studentsCount} élèves • ${schoolClass.hoursPerWeek}h/semaine"
                                )
                            }
                        },

                        leadingContent = {

                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {

                                Icon(
                                    Icons.Default.School,
                                    null,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CurriculumProgressSection(
    progress: List<CurriculumProgressUi>
) {

    SectionCard(
        title = "Progression du programme",
        icon = Icons.AutoMirrored.Filled.TrendingUp
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            progress.forEach {

                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Text(
                            it.className,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            "${(it.currentProgress * 100).toInt()}%"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { it.currentProgress },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        "Programme national : ${(it.nationalProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TeachingScheduleSection() {

    SectionCard(
        title = "Horaire d'enseignement",
        icon = Icons.Default.CalendarMonth
    ) {
        val currentDate = remember { LocalDate.now() }
        val startDate = remember { currentDate.minusDays(500) }
        val endDate = remember { currentDate.plusDays(500) }
        var selection by remember { mutableStateOf(currentDate) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme .background),
        ) {
            val state = rememberWeekCalendarState(
                startDate = startDate,
                endDate = endDate,
                firstVisibleWeekDate = currentDate,
            )

            Row {
                Column(modifier = Modifier.width(88.dp)) {
                    Box(
                        modifier = Modifier.height(59.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Heures",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Light,
                        )
                    }
                    HorizontalDivider()
                    schedulingLabel.forEach {
                        ListItem(
                            modifier = Modifier.background(if (it == "10h16-10h30") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background),
                            headlineContent = {
                                Text(it, fontSize = 9.sp)
                            }
                        )
                        HorizontalDivider()
                    }
                }

                WeekCalendar(
                    modifier = Modifier.background(color = MaterialTheme.colorScheme .background),
                    state = state,
                    dayContent = { day ->

                        Day(day.date, isSelected = selection == day.date) { clicked ->
                            if (selection != clicked) {
                                selection = clicked
                            }
                        }
                    },
                )
            }


        }

    }
}

@Composable
private fun RecentActivitiesSection(
    activities: List<TeacherActivityUi>
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

private val dateFormatter by lazy {
    LocalDate.Format {
        day(padding = Padding.ZERO)
    }
}

@Composable
private fun Day(date: LocalDate, isSelected: Boolean, onClick: (LocalDate) -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clickable { onClick(date) },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = dayOfWeekInFrench(date.dayOfWeek.ordinal),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Light,
                )
                Text(
                    text = dateFormatter.format(date),
                    fontSize = 14.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer)
                        .align(Alignment.BottomCenter),
                )

            }
            HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))

        }
        schedulingLabel.forEach {
            ListItem(
                modifier = Modifier.background(if (it == "10h16-10h30") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background),
                headlineContent = {
                    //Text(it, fontSize = 9.sp)
                }
            )
            HorizontalDivider()
        }
    }

}

fun dayOfWeekInFrench(numberOfDay : Int): String {
    return when (numberOfDay) {
        0 -> "Ln"
        1 -> "Mar"
        2 -> "Mer"
        3 -> "Jeu"
        4 -> "Ven"
        5 -> "Sam"
        6 -> "Dim"
        else -> "Unknow"
    }
}