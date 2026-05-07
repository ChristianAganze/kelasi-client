package com.drcmind.kelasisuite.ui.schooladmin.AcademicManagement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.ui.components.AppIcons

@Composable
fun ClassDetailsScreen(
    classId: Int,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            DetailTopBar(onBack)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Action ajout */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 32.dp, top = 16.dp)
        ) {
            // 1. SECTION HERO (Bento)
            item {
                ClassHeroSection()
            }

            // 2. ONGLETS DE NAVIGATION
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        TabItem("Liste des élèves", active = true)
                        TabItem("Liste des cours", active = false)
                        TabItem("Enseignants", active = false)
                    }
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            // 3. EN-TÊTE DE LISTE
            item {
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
            }

            // 4. LISTE DES ÉLÈVES
            items(sampleStudents) { student ->
                StudentRowItem(student)
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.surfaceVariant)
            }

            // 5. BENTO GRID - VUE D'ENSEMBLE
            item {
                Text(
                    text = "VUE D'ENSEMBLE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DailyPlanningCard(modifier = Modifier.weight(1f))
                    TeacherMiniCard(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun DetailTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {
            Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Back to list",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Avatar Admin
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
        )
    }
}

@Composable
fun ClassHeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(32.dp))
            .padding(32.dp)
    ) {
        // Décoration d'arrière-plan
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)
                .size(120.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
        )

        Column {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    "SCIENTIFIQUE",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "3e année Scientifique B",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.5).sp,
                lineHeight = 38.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                HeroStat("ÉLÈVES", "38/40")
                HeroStat("MOYENNE", "14.2")
                HeroStat("LOCAL", "Lab 402")
            }
        }
    }
}

@Composable
fun HeroStat(label: String, value: String) {
    Column {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun StudentRowItem(student: Student) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(AppIcons.person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(student.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Student ID: ${student.id}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        val badgeColor = if(student.isActive) Color(0xFFE1FBEF) else MaterialTheme.colorScheme.errorContainer
        val contentColor = if(student.isActive) Color(0xFF0E6245) else MaterialTheme.colorScheme.onErrorContainer

        Surface(
            color = badgeColor,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
        ) {
            Text(
                text = if(student.isActive) "ACTIVE" else "PROBATION",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = contentColor
            )
        }
    }
}

@Composable
fun DailyPlanningCard(modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(24.dp)
    ) {
        Icon(AppIcons.curriculum, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Planning", color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text("08:00 - Chimie", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 11.sp)
    }
}

@Composable
fun TeacherMiniCard(modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp)
    ) {
        Icon(AppIcons.person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Prof. Jean", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text("Mathématiques", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
fun TabItem(text: String, active: Boolean) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (active) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.width(24.dp).height(2.dp).background(MaterialTheme.colorScheme.primary))
        }
    }
}

data class Student(val id: String, val name: String, val isActive: Boolean)
val sampleStudents = listOf(
    Student("#2024-001", "Alice Mbuyi", true),
    Student("#2024-002", "Marc Kalonji", false),
    Student("#2024-003", "Sophie Bakala", true)
)