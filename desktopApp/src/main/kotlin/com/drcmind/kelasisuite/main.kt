@file:JvmName("DesktopLauncher")
package com.drcmind.kelasisuite

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kelasi Suite",
    ) {
        App()
    }
}