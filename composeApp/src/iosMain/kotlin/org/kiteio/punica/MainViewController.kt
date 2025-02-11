package org.kiteio.punica

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.window.ComposeUIViewController

fun mainViewController() = ComposeUIViewController {
    App(widthSizeClass = WindowWidthSizeClass.Medium)
}