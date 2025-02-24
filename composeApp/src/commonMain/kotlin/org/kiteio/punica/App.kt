package org.kiteio.punica

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.kiteio.punica.ui.page.account.AccountPage
import org.kiteio.punica.ui.page.account.AccountRoute
import org.kiteio.punica.ui.page.home.HomePage
import org.kiteio.punica.ui.page.home.HomeRoute
import org.kiteio.punica.ui.theme.PunicaTheme
import org.kiteio.punica.wrapper.launchCatching

@Composable
fun App(windowSizeClass: WindowSizeClass) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val themeMode by AppVM.themeMode.collectAsState(
        runBlocking { AppVM.themeMode.first() }
    )
    val userId by AppVM.academicUserId.collectAsState(null)

    // 监听教务系统学号变化，更新教务系统
    LaunchedEffect(userId) { scope.launchCatching { AppVM.updateAcademicSystem(userId) } }

    PunicaTheme(
        themeMode = themeMode,
        windowSizeClass = windowSizeClass,
        navController = navController,
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
        ) {
            composable<HomeRoute> { HomePage() }
            composable<AccountRoute> { AccountPage(it.toRoute<AccountRoute>()) }
        }
    }
}