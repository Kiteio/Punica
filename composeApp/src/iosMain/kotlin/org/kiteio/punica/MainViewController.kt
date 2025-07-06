package org.kiteio.punica

import androidx.compose.ui.window.ComposeUIViewController
import org.kiteio.punica.mirror.App
import org.kiteio.punica.mirror.platform.Platform

fun mainViewController() = ComposeUIViewController {
    App(Platform.iOS)
}