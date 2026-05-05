package com.drcmind.kelasisuite

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.drcmind.kelasisuite.di.commonModule
import com.drcmind.kelasisuite.navigation.NavigationRoot

import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration(declaration = { modules(commonModule()) }), content = {
        MaterialTheme {
            NavigationRoot()
        }
    })
}