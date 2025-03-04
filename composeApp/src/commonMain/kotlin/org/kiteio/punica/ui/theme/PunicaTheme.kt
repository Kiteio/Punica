package org.kiteio.punica.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.materialkolor.DynamicMaterialTheme
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
        DynamicMaterialTheme(
            primary = Color(0xff5b89e8),
            useDarkTheme = isDarkTheme,
            animate = true,
            content = content,
        )
    }
}