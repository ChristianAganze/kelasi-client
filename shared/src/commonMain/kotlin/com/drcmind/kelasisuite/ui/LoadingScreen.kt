package com.drcmind.kelasisuite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.ui.components.LoadingState

@Composable
fun LoadingScreen(
    message: String = "Chargement de votre espace Kelasi..."
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            LoadingState(message = message)
        }
    }
}
