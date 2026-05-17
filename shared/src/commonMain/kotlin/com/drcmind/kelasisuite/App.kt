package com.drcmind.kelasisuite

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.drcmind.kelasisuite.di.commonModule
import com.drcmind.kelasisuite.navigation.NavigationRoot
import com.drcmind.kelasisuite.ui.theme.KelasiSuiteTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration(declaration = { modules(commonModule()) }), content = {
        KelasiSuiteTheme {
            NavigationRoot()
        }
    })
}