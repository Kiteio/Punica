package org.kiteio.punica.mirror

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.kiteio.punica.mirror.platform.Platform
import org.kiteio.punica.mirror.ui.PunicaExpressiveTheme
import org.kiteio.punica.mirror.ui.pages.NavigationRoute
import org.kiteio.punica.mirror.ui.pages.navigationDestination

@Composable
fun App(platform: Platform) {
    val isDarkTheme = false
    val navController = rememberNavController()

    PunicaExpressiveTheme(
        isDarkTheme = isDarkTheme,
        modifyColorScheme = if (!isDarkTheme)
            platform::whiteBackgroundOnDesktop else null,
    ) {
        NavHost(
            navController = navController,
            startDestination = NavigationRoute,
        ) {
            navigationDestination()
        }
    }
}

/**
 * 当 [Platform] 为 [Platform.Desktop] 时，
 * 将 [ColorScheme] 的背景色设置为白色。
 */
private fun Platform.whiteBackgroundOnDesktop(
    colorScheme: ColorScheme,
): ColorScheme {
    return if (this == Platform.Desktop) {
        colorScheme.copy(
            background = Color.White,
            surface = Color.White,
        )
    } else {
        colorScheme
    }
}