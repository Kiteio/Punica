package org.kiteio.punica

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.ui.component.snackbarHostState
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.app_name
import punica.composeapp.generated.resources.punica

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.punica),
    ) {
        App(windowSizeClass = calculateWindowSizeClass(), snackbarHostState = snackbarHostState)
    }
}