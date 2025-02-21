package org.kiteio.punica.ui.page.home

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import org.jetbrains.compose.resources.stringResource

/**
 * 底部导航栏。
 *
 * @param routes 路由
 * @param navDestination 导航目的地
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigationBar(
    routes: List<TopLevelRoute>,
    onNavigate: (TopLevelRoute) -> Unit,
    navDestination: NavDestination?,
) {
    ShortNavigationBar {
        routes.forEach { route ->
            NavigationBarItem(
                selected = navDestination?.hierarchy?.any { it.hasRoute(route::class) } == true,
                onClick = { onNavigate(route) },
                route = route,
            )
        }
    }
}


/**
 * 底部导航栏项。
 *
 * @param selected 是否选中
 * @param route 路由
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    route: TopLevelRoute,
) {
    ShortNavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { ToggleRouteIcon(selected, topLevelRoute = route) },
        label = { ToggleText(selected, text = stringResource(route.nameRes)) },
    )
}