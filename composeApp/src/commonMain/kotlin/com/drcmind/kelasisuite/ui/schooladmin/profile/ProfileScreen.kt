package com.drcmind.kelasisuite.ui.schooladmin.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.dto.UserDTO
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if ((state.user != null) && (state.school != null)) {
                ProfileContent(state.user!!, state.school!!)
            }
        }
    }
}

@Composable
fun ProfileContent(user: UserDTO, school: SchoolDTO) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Page Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "School Profile",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage and view official educational establishment details.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Export Dossier", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Update Information", fontWeight = FontWeight.Bold)
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Left Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Admin Profile Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                // Placeholder for profile image
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().padding(32.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                            }
                            Surface(
                                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    null,
                                    modifier = Modifier.size(16.dp).padding(2.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "${user.firstName} ${user.lastName}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user.email ?: "no-email@kelasi.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(16.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = user.roles.firstOrNull()?.uppercase() ?: "ADMIN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Spacer(Modifier.height(32.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatMiniCard(label = "Status", value = if (user.isActive) "Connected" else "Offline", modifier = Modifier.weight(1f))
                            StatMiniCard(label = "Last Sync", value = "2h ago", modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Map Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(256.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black)
                ) {
                    // Image would go here
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
                    ) {
                        Text("Location", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Black)
                        Text("${school.address.cityTerritory}, ${school.address.province}", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Map, null, modifier = Modifier.size(40.dp).padding(8.dp), tint = Color.Black)
                    }
                }
            }

            // Right Column
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // School Main Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(32.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalance, null, modifier = Modifier.size(40.dp), tint = Color.White)
                            }
                            Spacer(Modifier.width(24.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(school.officialName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(12.dp))
                                    Surface(
                                        color = Color(0xFFE6F4EA),
                                        shape = CircleShape,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCEEAD6))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(Modifier.size(6.dp).background(Color(0xFF34A853), CircleShape))
                                            Spacer(Modifier.width(6.dp))
                                            Text("ACTIVE / AGRÉÉ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color(0xFF0D652D))
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    IconText(Icons.Default.Tag, "ID: KLS-2024-001")
                                    IconText(Icons.Default.Public, "${school.address.cityTerritory}, DRC")
                                }
                            }
                        }

                        Spacer(Modifier.height(40.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("GENERAL INFO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                                InfoRow("Legal Status", school.legalStatus)
                                InfoRow("Authority", school.supervisoryAuthority)
                                InfoRow("Nat. ID Number", school.nationalIdentificationNumber ?: "N/A")
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("CONTACT DETAILS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                                InfoRow("Phone", school.phone ?: "N/A")
                                InfoRow("Email", school.email ?: "N/A")
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Text("Address", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "${school.address.streetAndNumber}, ${school.address.neighborhood}, ${school.address.cityTerritory}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Bento Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    BentoCard(
                        icon = Icons.Default.VerifiedUser,
                        label = "Accreditation",
                        value = school.accreditationNumber,
                        modifier = Modifier.weight(1f)
                    )
                    BentoCard(
                        icon = Icons.Default.Approval,
                        label = "Approval Number",
                        value = school.approvalNumber ?: "N/A",
                        date = "12/03/2023",
                        isDark = true,
                        modifier = Modifier.weight(1f)
                    )
                    BentoCard(
                        icon = Icons.Default.TaskAlt,
                        label = "Authorization",
                        value = school.operatingAuthorizationNumber ?: "N/A",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Documents Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(32.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Important Documents", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { }) {
                                Text("View All", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                DocumentItem(Icons.Default.Description, "School_Statutes_2024.pdf", "Legal Document • 2.4 MB", Modifier.weight(1f))
                                DocumentItem(Icons.Default.HistoryEdu, "Operating_Permit.pdf", "Accreditation • 1.1 MB", Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                DocumentItem(Icons.Default.AssignmentTurnedIn, "Quality_Certification.pdf", "Academic • 3.8 MB", Modifier.weight(1f))
                                DocumentItem(Icons.Default.AccountBalance, "Organogram_Structure.png", "Management • 0.9 MB", Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatMiniCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun IconText(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).border(width = 0.dp, color = Color.Transparent).padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
}

@Composable
fun BentoCard(
    icon: ImageVector,
    label: String,
    value: String,
    date: String? = null,
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1B1B1B) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = if (isDark) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Icon(icon, null, modifier = Modifier.size(32.dp), tint = if (isDark) Color.White else MaterialTheme.colorScheme.primary)
                if (date != null) {
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(date, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column {
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = if (isDark) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun DocumentItem(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
        }
        Icon(Icons.Default.Download, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.outline)
    }
}
