package org.kiteio.punica.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowSizeClass
import com.materialkolor.ktx.animateColorScheme
import com.materialkolor.rememberDynamicColorScheme
import org.kiteio.punica.ui.compositionlocal.LocalIsDarkTheme
import org.kiteio.punica.ui.compositionlocal.LocalNavController
import org.kiteio.punica.ui.compositionlocal.LocalWindowSizeClass
import org.kiteio.punica.ui.theme.ThemeMode.Dark
import org.kiteio.punica.ui.theme.ThemeMode.Light

/**
 * 小石榴主题。
 *
 * @param themeMode 主题模式
 * @param windowSizeClass 窗口尺寸等级
 * @param navController 导航控制器。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PunicaTheme(
    themeMode: ThemeMode?,
    windowSizeClass: WindowSizeClass,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    val isDarkTheme = when (themeMode) {
        Light -> false
        Dark -> true
        else -> isSystemInDarkTheme()
    }

    CompositionLocalProvider(
        LocalWindowSizeClass provides windowSizeClass,
        LocalNavController provides navController,
        LocalIsDarkTheme provides isDarkTheme,
    ) {
        PunicaTheme(isDarkTheme = isDarkTheme, content = content)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PunicaTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = rememberDynamicColorScheme(
        primary = Color(0xff5b89e8),
        isAmoled = true,
        isDark = isDarkTheme,
    )
    MaterialExpressiveTheme(
        colorScheme = animateColorScheme(colorScheme),
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}

val ColorScheme.link: Color
    get() = Color(0xFF379EDC)