package org.kiteio.punica.ui.compositionlocal

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.compositionLocalOf

/**
 * 窗口尺寸等级。
 */
val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("CompositionLocal WindowSizeClass not present")
}


/**
 * 宽度尺寸等级是否为 [WindowWidthSizeClass.Compact]。
 */
val WindowSizeClass.isCompactWidth
    get() = widthSizeClass == WindowWidthSizeClass.Compact


/**
 * 高度尺寸等级是否为 [WindowHeightSizeClass.Medium]。
 */
val WindowSizeClass.isMediumHeight
    get() = heightSizeClass <= WindowHeightSizeClass.Medium