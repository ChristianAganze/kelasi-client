@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
package com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.enrollment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentDto

@Composable
fun EnrollmentDetailScreen(
    enrollment: EnrollmentDto,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onPrintCard: () -> Unit,
    onPrintConfirmation: () -> Unit,
    onTransfer: () -> Unit
) {
    Scaffold(
        topBar = {
            EnrollmentDetailTopBar(
                enrollment = enrollment,
                onBack = onBack,
                onEdit = onEdit,
                onPrintCard = onPrintCard,
                onPrintConfirmation = onPrintConfirmation,
                onTransfer = onTransfer
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                StudentInfoSection(
                    enrollment = enrollment
                )
            }
            item {
                AcademicPlacementSection(
                    enrollment = enrollment
                )
            }
            item {
                FinancialStatusSection()
            }
            item {
                GuardianSection()
            }
            item {
                EnrollmentTimelineSection()
            }
        }
    }
}


@Composable
private fun EnrollmentDetailTopBar(
    enrollment: EnrollmentDto,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onPrintCard: () -> Unit,
    onPrintConfirmation: () -> Unit,
    onTransfer: () -> Unit
) {

    TopAppBar(
        title = {

            Column {

                Text(
                    "${enrollment.student.firstName} ${enrollment.student.lastName}",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Dossier d'inscription",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },

        navigationIcon = {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    null
                )
            }
        },

        actions = {

            IconButton(
                onClick = onEdit
            ) {

                Icon(
                    Icons.Default.Edit,
                    contentDescription = null
                )
            }

            IconButton(
                onClick = onPrintCard
            ) {
                Icon(
                    Icons.Default.Badge,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onTransfer
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
        }
    )
}


@Composable
private fun StudentInfoSection(
    enrollment: EnrollmentDto
) {

    DetailSectionCard(
        title = "Informations de l'élève",
        icon = Icons.Default.Person
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        Icons.Default.Person,
                        null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column {

                Text(
                    "${enrollment.student.firstName} ${enrollment.student.lastName}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Inscription confirmée",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        InfoGrid(
            label1 = "Date d'inscription",
            value1 = "12 Septembre 2026",
            label2 = "Année académique",
            value2 = enrollment.academicYear.label
        )
    }
}

/*
========================================================
SECTION : ACADEMIC PLACEMENT
========================================================
*/

@Composable
private fun AcademicPlacementSection(
    enrollment: EnrollmentDto
) {

    DetailSectionCard(
        title = "Placement académique",
        icon = Icons.Default.School
    ) {

        InfoGrid(
            label1 = "Cycle",
            value1 = enrollment.schoolClass.schoolSection,
            label2 = "Section",
            value2 = enrollment.schoolClass.section
        )

        Spacer(modifier = Modifier.height(20.dp))

        InfoGrid(
            label1 = "Option",
            value1 = enrollment.schoolClass.major,
            label2 = "Niveau",
            value2 = enrollment.schoolClass.gradeLevel
        )

        Spacer(modifier = Modifier.height(20.dp))

        InfoItem(
            label = "Classe",
            value = enrollment.schoolClass.name
        )
    }
}


@Composable
private fun FinancialStatusSection() {

    DetailSectionCard(
        title = "Situation financière",
        icon = Icons.Default.Payments
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            FinancialCard(
                modifier = Modifier.weight(1f),
                title = "Frais payés",
                amount = "350 USD / 450 USD",
                progress = 0.75f
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        AssistChip(
            onClick = {},
            label = {
                Text("Paiement à jour")
            },
            leadingIcon = {

                Icon(
                    Icons.Default.CheckCircle,
                    null
                )
            }
        )
    }
}

@Composable
private fun GuardianSection() {

    DetailSectionCard(
        title = "Parent & contact d'urgence",
        icon = Icons.Default.Groups
    ) {

        InfoGrid(
            label1 = "Nom du parent",
            value1 = "Jean Mukendi",
            label2 = "Téléphone",
            value2 = "+243 999 000 111"
        )

        Spacer(modifier = Modifier.height(20.dp))

        InfoGrid(
            label1 = "Contact d'urgence",
            value1 = "Marie Kabeya",
            label2 = "Téléphone urgence",
            value2 = "+243 888 777 666"
        )

        Spacer(modifier = Modifier.height(20.dp))

        InfoItem(
            label = "Adresse",
            value = "Kinshasa, Gombe"
        )
    }
}

@Composable
private fun EnrollmentTimelineSection() {

    DetailSectionCard(
        title = "Chronologie d'inscription",
        icon = Icons.Default.Timeline
    ) {

        TimelineItem(
            title = "Soumission de candidature",
            date = "02 Sept 2026",
            completed = true
        )

        TimelineItem(
            title = "Vérification des documents",
            date = "03 Sept 2026",
            completed = true
        )

        TimelineItem(
            title = "Dépôt des frais",
            date = "05 Sept 2026",
            completed = true
        )

        TimelineItem(
            title = "Validation académique",
            date = "06 Sept 2026",
            completed = true
        )

        TimelineItem(
            title = "Confirmation d'inscription",
            date = "07 Sept 2026",
            completed = true,
            isLast = true
        )
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {

    OutlinedCard{

        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {

                    Icon(
                        icon,
                        null,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            content()
        }
    }
}

@Composable
private fun InfoGrid(
    label1: String,
    value1: String,
    label2: String,
    value2: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                label1,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                value1,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                label2,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                value2,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String
) {

    Column {

        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
private fun FinancialCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    progress: Float
) {

    Card(
        modifier = modifier
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                amount,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TimelineItem(
    title: String,
    date: String,
    completed: Boolean,
    isLast: Boolean = false
) {

    Row {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                modifier = Modifier.size(18.dp),
                shape = CircleShape,
                color = if (completed)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ) {}

            if (!isLast) {

                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(56.dp)
                        .background(
                            MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}