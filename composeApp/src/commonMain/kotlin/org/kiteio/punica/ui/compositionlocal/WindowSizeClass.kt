package org.kiteio.punica.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import androidx.window.core.layout.WindowSizeClass

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
    get() = isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)


/**
 * 高度尺寸等级是否为 [WindowHeightSizeClass.Medium]。
 */
val WindowSizeClass.isMediumHeight
    get() = isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)