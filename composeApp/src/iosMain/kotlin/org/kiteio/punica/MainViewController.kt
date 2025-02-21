package org.kiteio.punica

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.ComposeUIViewController

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun mainViewController() = ComposeUIViewController {
    App(windowSizeClass = WindowSizeClass.calculateFromSize(DpSize.Unspecified))
}