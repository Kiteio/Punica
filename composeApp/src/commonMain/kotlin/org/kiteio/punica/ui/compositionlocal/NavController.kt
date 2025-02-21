package org.kiteio.punica.ui.compositionlocal

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController


/**
 * 导航控制器。
 */
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("CompositionLocal WindowSizeClass not present")
}