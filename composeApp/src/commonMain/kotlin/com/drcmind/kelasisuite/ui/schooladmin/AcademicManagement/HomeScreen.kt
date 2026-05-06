package com.drcmind.kelasisuite.ui.schooladmin.AcademicManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.ui.components.AppColors
import com.drcmind.kelasisuite.ui.components.AppIcons

@Composable
fun HomeScreen(
    onBack: () -> Unit,
    onAddClass: () -> Unit,
    onSelectClass: (Int) -> Unit
) {
    val classes = listOf(
        ClassItem(101, "6ème Littéraire A", "Complexe Scolaire Horizon", "Secondaire", 30),
        ClassItem(102, "5ème Scientifique B", "Institut des Sciences", "Secondaire", 25),
        ClassItem(103, "4ème Math-Physique", "Lycée Prince de Liège", "Secondaire", 18)
    )

    Scaffold(
        containerColor = AppColors.surfaceBackground,
        topBar = {
            TopHeader(onBack, onAddClass)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp)
        ) {
            // Fil d'ariane (Breadcrumb)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = "ACADÉMIE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = AppColors.textSecondary
                )
                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    modifier = Modifier.size(12.dp),
                    tint = AppColors.textIcon
                )
                Text(
                    text = "GESTION DES CLASSES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = AppColors.primary
                )
            }

            Text(
                text = "Annuaire des Classes",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.5).sp,
                color = AppColors.textPrimary
            )
            Text(
                text = "Gérez et organisez les sections académiques pour l'année 2024-2025.",
                color = AppColors.textSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Grille Bento
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(classes) { item ->
                    AcademicClassCard(
                        item = item,
                        onClick = { onSelectClass(item.id) }
                    )
                }

                item {
                    AddClassEmptyCard(onClick = onAddClass)
                }
            }
        }
    }
}

@Composable
fun TopHeader(onBack: () -> Unit, onAddClass: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(AppColors.surfaceContainer)
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Retour", tint = AppColors.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Barre de recherche
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.surfaceInput)
                    .border(1.dp, AppColors.surfaceBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = AppColors.textIcon,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rechercher une classe...",
                        color = AppColors.textPlaceholder,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Button(
            onClick = onAddClass,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Button.primary,
                contentColor = AppColors.Button.primaryText
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ajouter une classe", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AcademicClassCard(item: ClassItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .border(1.dp, AppColors.surfaceBorder, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = AppColors.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Icône Bento utilisant AppIcons
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(AppColors.surfaceContainerLow, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.school,
                        contentDescription = null,
                        tint = AppColors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Badge Catégorie
                Surface(
                    color = AppColors.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = item.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
            Text(
                text = item.subtitle,
                fontSize = 14.sp,
                color = AppColors.textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        AppIcons.person,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = AppColors.textIcon
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.studentCount} Élèves",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textTertiary
                    )
                }

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.surfaceContainerLow,
                        contentColor = AppColors.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text("Détails", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddClassEmptyCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .border(2.dp, AppColors.outlineVariant, RoundedCornerShape(24.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, AppColors.textIcon, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = AppColors.textIcon)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Créer une nouvelle classe",
                fontWeight = FontWeight.Bold,
                color = AppColors.textSecondary
            )
            Text(
                text = "SECTION 2024",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = AppColors.textPlaceholder
            )
        }
    }
}

data class ClassItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val category: String,
    val studentCount: Int
)