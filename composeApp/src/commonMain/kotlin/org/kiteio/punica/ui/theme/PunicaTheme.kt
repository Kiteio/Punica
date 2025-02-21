package org.kiteio.punica.ui.theme

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.materialkolor.DynamicMaterialTheme
import org.kiteio.punica.ui.compositionlocal.LocalNavController
import org.kiteio.punica.ui.compositionlocal.LocalWindowSizeClass

/**
 * 小石榴主题。
 *
 * @param isDarkTheme 是否为暗色主题
 * @param windowSizeClass 窗口尺寸等级
 * @param navController 导航控制器。
 */
@Composable
fun PunicaTheme(
    isDarkTheme: Boolean,
    windowSizeClass: WindowSizeClass,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalWindowSizeClass provides windowSizeClass,
        LocalNavController provides navController,
    ) {
        DynamicMaterialTheme(
            primary = Color(0xFF5783E0),
            useDarkTheme = isDarkTheme,
            animate = true,
            content = content,
        )
    }
}