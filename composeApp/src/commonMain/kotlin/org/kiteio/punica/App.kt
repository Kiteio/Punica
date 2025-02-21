package org.kiteio.punica

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.kiteio.punica.ui.page.home.HomePage
import org.kiteio.punica.ui.page.home.HomeRoute
import org.kiteio.punica.ui.theme.PunicaTheme

@Composable
fun App(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()

    PunicaTheme(
        isDarkTheme = AppVM.isDarkTheme(),
        windowSizeClass = windowSizeClass,
        navController = navController,
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
        ) {
            composable<HomeRoute> { HomePage() }
        }
    }
}