package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TeacherProfileTopBar(
    teacherName: String,
    onBack: () -> Unit
) {

    TopAppBar(
        title = {
            Column {
                Text(
                    teacherName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Profil enseignant",
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
            IconButton(onClick = {}) {
                Icon(Icons.Default.Edit, null)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Print, null)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Share, null)
            }
        }
    )
}

@Composable
fun TeacherHeaderSection(
    teacherName: String,
    specialization : String,
    grade : String,
    numberOfClasses : Int,
    experienceYears : Int,
    totalHoursPerWeek : Int,

) {
    OutlinedCard(
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(92.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        teacherName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        specialization,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(grade)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Work, null)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                StatisticCard(
                    modifier = Modifier.weight(1f),
                    title = "Classes",
                    value = "$numberOfClasses"
                )

                StatisticCard(
                    modifier = Modifier.weight(1f),
                    title = "Expérience",
                    value = "$experienceYears ans"
                )

                StatisticCard(
                    modifier = Modifier.weight(1f),
                    title = "Charge horaire",
                    value = "${totalHoursPerWeek }h"
                )
            }
        }
    }
}



@Composable
private fun StatisticCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {

    OutlinedCard(
        modifier = modifier
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}