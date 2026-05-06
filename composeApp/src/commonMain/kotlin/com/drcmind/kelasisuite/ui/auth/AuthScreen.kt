package com.drcmind.kelasisuite.ui.auth

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AuthScreen(
    onAuthSuccess : ()->Unit
){
    Button(onClick = onAuthSuccess){
        Text("Goooooooooooooooo")
    }
}