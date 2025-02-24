package org.kiteio.punica.ui.page.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.ui.compositionlocal.LocalIsDarkTheme
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.theme_mode

/**
 * 侧边导航轨道。
 *
 * @param routes 路由
 * @param navDestination 导航目的地
 */
@Composable
fun NavigationRail(
    routes: List<TopLevelRoute>,
    onNavigate: (TopLevelRoute) -> Unit,
    navDestination: NavDestination?,
) {
    val scope = rememberCoroutineScope()
    val isDarkTheme = LocalIsDarkTheme.current

    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        header = {
            // 主题模式切换按钮
            IconButton(onClick = { scope.launchCatching { AppVM.switchTheme(isDarkTheme) } }) {
                Icon(
                    if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                    contentDescription = stringResource(Res.string.theme_mode),
                )
            }
        }
    ) {
        routes.forEach { route ->
            NavigationRailItem(
                selected = navDestination?.hierarchy?.any { it.hasRoute(route::class) } == true,
                onClick = { onNavigate(route) },
                route = route
            )
        }
    }
}


/**
 * 侧边导航轨道项。
 *
 * @param selected 是否选中
 * @param route 路由
 */
@Composable
private fun NavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    route: TopLevelRoute,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = { ToggleRouteIcon(selected, topLevelRoute = route) },
        label = { ToggleText(selected, text = stringResource(route.nameRes)) },
    )
}