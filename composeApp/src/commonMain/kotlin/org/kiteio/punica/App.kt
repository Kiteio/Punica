package org.kiteio.punica

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.kiteio.punica.ui.page.account.AccountPage
import org.kiteio.punica.ui.page.account.AccountRoute
import org.kiteio.punica.ui.page.call.EmergencyCallPage
import org.kiteio.punica.ui.page.call.EmergencyCallRoute
import org.kiteio.punica.ui.page.cet.CETPage
import org.kiteio.punica.ui.page.cet.CETRoute
import org.kiteio.punica.ui.page.grades.GradesPage
import org.kiteio.punica.ui.page.grades.GradesRoute
import org.kiteio.punica.ui.page.home.HomePage
import org.kiteio.punica.ui.page.home.HomeRoute
import org.kiteio.punica.ui.page.notice.AcademicNoticePage
import org.kiteio.punica.ui.page.notice.AcademicNoticeRoute
import org.kiteio.punica.ui.page.secondclass.SecondClassPage
import org.kiteio.punica.ui.page.secondclass.SecondClassRoute
import org.kiteio.punica.ui.page.websites.WebsitesPage
import org.kiteio.punica.ui.page.websites.WebsitesRoute
import org.kiteio.punica.ui.theme.PunicaTheme
import org.kiteio.punica.wrapper.LaunchedEffectCatching

@Composable
fun App(windowSizeClass: WindowSizeClass, snackbarHostState: SnackbarHostState? = null) {
    val navController = rememberNavController()
    val themeMode by AppVM.themeMode.collectAsState(
        runBlocking { AppVM.themeMode.first() }
    )
    val userId by AppVM.academicUserId.collectAsState(null)

    // 监听教务系统学号变化，更新教务系统
    LaunchedEffectCatching(userId) { AppVM.updateAcademicSystem(userId) }

    Scaffold(
        snackbarHost = { if (snackbarHostState != null) SnackbarHost(hostState = snackbarHostState) },
    ) {
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
                composable<EmergencyCallRoute> { EmergencyCallPage() }
                composable<AcademicNoticeRoute> { AcademicNoticePage() }
                composable<WebsitesRoute> { WebsitesPage() }
                composable<CETRoute> { CETPage() }
                composable<GradesRoute> { GradesPage() }
                composable<SecondClassRoute> { SecondClassPage() }
            }
        }
    }
}