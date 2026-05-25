@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)

package com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ListItemDefaults.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.ui.schooladmin.component.InfoChip
import com.drcmind.kelasisuite.ui.schooladmin.component.SectionCard

@Composable
fun StudentDetailScreen(
    student: StudentDTO?,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    isLoading : Boolean,
    error : String?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = student?.fullName ?: "Elève",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { student?.let { onEdit(it.id) } }
                    ) {
                        Icon(Icons.Default.Edit, null)
                    }

                    IconButton(
                        onClick = { }
                    ) {
                        Icon(Icons.Default.Print, null)
                    }

                    IconButton(
                        onClick = { }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.NoteAdd, null)
                    }
                }
            )
        }
    ) { padding ->

        when {

            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            student != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    StudentHeroCard(
                        student = student,
                        onEdit = {
                            onEdit(student.id)
                        }
                    )

                    AdaptiveInfoGrid(
                        student = student
                    )

                    EnrollmentTimelineCard(student)

                    DocumentsCard()
                }
            }
        }
    }
}

@Composable
private fun StudentHeroCard(
    student: StudentDTO,
    onEdit: () -> Unit
) {

    OutlinedCard (
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(84.dp),
                    shape = CircleShape,
                    tonalElevation = 6.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(22.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = student.fullName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "SERNIE ${student.sernieNumber ?: "--"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(student.status.name)
                        },
                        leadingIcon = {

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                        }
                    )
                }

                FilledTonalButton(
                    onClick = onEdit
                ) {

                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Edit")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(20.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                InfoChip(
                    icon = Icons.Default.Badge,
                    label = student.studentIdNumber
                )

                InfoChip(
                    icon = Icons.Default.School,
                    label = student.currentEnrollment?.className ?: "No class"
                )

                InfoChip(
                    icon = Icons.Default.CalendarMonth,
                    label = student.currentEnrollment?.gradeLevel ?: "--"
                )
            }
        }
    }
}

@Composable
private fun AdaptiveInfoGrid(
    student: StudentDTO
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        SectionCard(
            title = "Personal Information",
            icon = Icons.Default.Person
        ) {

            DetailItem(
                label = "Full Name",
                value = student.fullName
            )

            DetailItem(
                label = "Date of Birth",
                value = student.dateOfBirth.toString()
            )

            DetailItem(
                label = "Religion",
                value = student.religion ?: "N/A"
            )

            DetailItem(
                label = "Address",
                value = student.address ?: "N/A"
            )
        }

        SectionCard(
            title = "Academic",
            icon = Icons.Default.School
        ) {

            DetailItem(
                label = "Current Class",
                value = student.currentEnrollment?.className ?: "--"
            )

            DetailItem(
                label = "Grade Level",
                value = student.currentEnrollment?.gradeLevel ?: "--"
            )

            DetailItem(
                label = "Previous School",
                value = student.previousSchool ?: "N/A"
            )
        }

        PerformanceCard()

        SectionCard(
            title = "Support & Logistics",
            icon = Icons.Default.SupportAgent,
        ) {

            DetailItem(
                label = "Bus Route",
                value = "#42 North Sector"
            )

            DetailItem(
                label = "Locker",
                value = "B-204"
            )

            DetailItem(
                label = "Counselor",
                value = "Marcus T. Vance"
            )
        }
    }
}

@Composable
private fun PerformanceCard() {

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    "Academic Performance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "78%",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { 0.78f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "+2.4% from previous term",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EnrollmentTimelineCard(
    student: StudentDTO
) {
    SectionCard(
        title = "Enrollment History",
        icon = Icons.Default.History
    ) {

        TimelineItem(
            title = "Current Enrollment",
            subtitle = student.currentEnrollment?.className ?: "--",
            date = "2025"
        )

        HorizontalDivider()

        TimelineItem(
            title = "Transferred",
            subtitle = student.previousSchool ?: "Previous school",
            date = "2024"
        )

        HorizontalDivider()

        TimelineItem(
            title = "Initial Registration",
            subtitle = "Student officially enrolled",
            date = "2023"
        )
    }
}

@Composable
private fun DocumentsCard() {

    SectionCard(
        title = "Documents",
        icon = Icons.Default.Description
    ) {

        DocumentItem(
            title = "Medical_Release_2025.pdf",
            subtitle = "Signed June 2025"
        )

        HorizontalDivider()

        DocumentItem(
            title = "Academic_Report_T2.pdf",
            subtitle = "Issued March 2025"
        )
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {

    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}



@Composable
private fun TimelineItem(
    title: String,
    subtitle: String,
    date: String
) {

    ListItem(
        headlineContent = {
            Text(title)
        },
        supportingContent = {
            Text(subtitle)
        },
        trailingContent = {
            Text(
                date,
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingContent = {

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                Icon(
                    Icons.Default.Check,
                    null,
                    modifier = Modifier.padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        colors = colors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun DocumentItem(
    title: String,
    subtitle: String
) {

    ListItem(
        headlineContent = {
            Text(title)
        },
        supportingContent = {
            Text(subtitle)
        },
        leadingContent = {
            Icon(
                Icons.Default.Description,
                null
            )
        },
        trailingContent = {
            IconButton(
                onClick = { }
            ) {
                Icon(
                    Icons.Default.Download,
                    null
                )
            }
        },
        colors = colors(
            containerColor = Color.Transparent
        )
    )
}