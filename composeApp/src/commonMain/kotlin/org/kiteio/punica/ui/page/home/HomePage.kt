package org.kiteio.punica.ui.page.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.kiteio.punica.ui.compositionlocal.LocalWindowSizeClass
import org.kiteio.punica.ui.compositionlocal.isCompactWidth
import org.kiteio.punica.ui.page.modules.ModulesPage
import org.kiteio.punica.ui.page.modules.ModulesRoute
import org.kiteio.punica.ui.page.settings.SettingsPage
import org.kiteio.punica.ui.page.settings.SettingsRoute
import org.kiteio.punica.ui.page.timetable.TimetablePage
import org.kiteio.punica.ui.page.timetable.TimetableRoute

/**
 * 首页路由。
 */
@Serializable
object HomeRoute


/**
 * 首页页面。
 */
@Composable
fun HomePage() = Content()


@Composable
fun Content() {
    val windowSizeClass = LocalWindowSizeClass.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val routes = listOf(TimetableRoute, ModulesRoute, SettingsRoute)

    Navigation(
        useRail = !windowSizeClass.isCompactWidth,
        routes = routes,
        navDestination = backStackEntry?.destination,
        onNavigate = { navController.navigate(it) },
    ) {
        NavHost(
            navController = navController,
            startDestination = TimetableRoute,
        ) {
            composable<TimetableRoute> { TimetablePage() }
            composable<ModulesRoute> { ModulesPage() }
            composable<SettingsRoute> { SettingsPage() }
        }
    }
}


/**
 * 导航栏。
 *
 * @param useRail 是否使用侧边导航栏
 * @param routes 路由
 * @param navDestination 导航目的地
 * @param onNavigate 导航事件
 */
@Composable
private fun Navigation(
    useRail: Boolean,
    routes: List<TopLevelRoute>,
    navDestination: NavDestination?,
    onNavigate: (TopLevelRoute) -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        bottomBar = {
            // 底部导航栏
            AnimatedVisibility(
                !useRail,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
            ) {
                NavigationBar(
                    routes = routes,
                    onNavigate = onNavigate,
                    navDestination = navDestination,
                )
            }
        },
        contentWindowInsets = WindowInsets.captionBar,
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {
            // 侧边导航轨道
            AnimatedVisibility(
                useRail,
                enter = fadeIn() + slideInHorizontally(),
                exit = fadeOut() + slideOutHorizontally(),
            ) {
                NavigationRail(
                    routes = routes,
                    onNavigate = onNavigate,
                    navDestination = navDestination,
                )
            }

            // 页面内容
            Surface(modifier = Modifier.weight(1f)) { content() }
        }
    }
}


/**
 * 顶层路由。
 *
 * @property nameRes 名称字符串资源
 * @property icon 变换前图标
 * @property toggledIcon 变换后图标
 */
interface TopLevelRoute {
    val nameRes: StringResource
    val icon: ImageVector
    val toggledIcon: ImageVector
}