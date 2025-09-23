package org.kiteio.punica

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.App
import org.kiteio.punica.mirror.platform.Platform
import org.kiteio.punica.mirror.ui.snackbarHostState
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
        App(
            platform = Platform.Desktop,
            snackbarHostState = snackbarHostState,
        )
    }
}