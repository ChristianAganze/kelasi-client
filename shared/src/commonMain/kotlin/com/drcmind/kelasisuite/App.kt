package com.drcmind.kelasisuite

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.drcmind.kelasisuite.di.commonModule
import com.drcmind.kelasisuite.navigation.NavigationRoot
import com.drcmind.kelasisuite.ui.theme.KelasiSuiteTheme
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = { modules(commonModule()) }) {
        KelasiSuiteTheme {
            NavigationRoot()
        }
    }
}
