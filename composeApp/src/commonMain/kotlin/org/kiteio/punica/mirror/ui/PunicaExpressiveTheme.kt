package org.kiteio.punica.mirror.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.rememberDynamicColorScheme

/**
 * 支持 [MaterialExpressiveTheme] 的 Punica 主题。
 *
 * @param isDarkTheme 是否为暗色主题
 * @param modifyColorScheme 修改颜色方案
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PunicaExpressiveTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    modifyColorScheme: ((ColorScheme) -> ColorScheme)? = null,
    content: @Composable () -> Unit,
) {
    val colorScheme = rememberDynamicColorScheme(
        primary = Color(0xff5b89e8),
        isAmoled = false,
        isDark = isDarkTheme,
        modifyColorScheme = modifyColorScheme,
    )

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}