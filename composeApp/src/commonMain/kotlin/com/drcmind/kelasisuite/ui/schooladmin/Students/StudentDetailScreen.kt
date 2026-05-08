package com.drcmind.kelasisuite.ui.schooladmin.Students

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    studentId: Long,
    onBack: () -> Unit,
    viewModel: StudentDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadStudent(studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails de l'élève") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(state.error!!, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
            } else if (state.student != null) {
                val student = state.student!!
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Nom complet: ${student.fullName}", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Matricule: ${student.studentIdNumber}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Classe: ${student.currentEnrollment?.className ?: "Non assigné"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Adresse: ${student.address ?: "N/A"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("École de provenance: ${student.previousSchool}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Statut: ${student.status}")
                }
            }
        }
    }
}
