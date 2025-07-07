package org.kiteio.punica.mirror.ui.pages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.Companion.NavigationBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.Companion.ShortNavigationBarCompact
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.Companion.ShortNavigationBarMedium
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.util.fastForEach
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import compose.icons.TablerIcons
import compose.icons.tablericons.Sailboat
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kiteio.punica.mirror.ui.PunicaExpressiveTheme
import org.kiteio.punica.mirror.ui.navigation.NavDescription
import org.kiteio.punica.mirror.ui.pages.navigation.ModuleRoute
import org.kiteio.punica.mirror.ui.pages.navigation.SettingRoute
import org.kiteio.punica.mirror.ui.pages.navigation.TimetableRoute
import org.kiteio.punica.mirror.ui.pages.navigation.moduleDestination
import org.kiteio.punica.mirror.ui.pages.navigation.settingDestination
import org.kiteio.punica.mirror.ui.pages.navigation.timetableDestination
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.modules
import punica.composeapp.generated.resources.settings
import punica.composeapp.generated.resources.timetable

/** 导航页面路由 */
@Serializable
data object NavigationRoute

/**
 * 导航页面目的地。
 */
fun NavGraphBuilder.navigationDestination() {
    composable<NavigationRoute> {
        NavigationPage()
    }
}

@Preview
@Composable
fun PreviewNavigationPage() {
    PunicaExpressiveTheme {
        NavigationPage()
    }
}

@Composable
fun NavigationPage() {
    val navController = rememberNavController()
    val navigationSuiteType = rememberNavigationSuiteType()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navDescriptions = listOf(
        NavDescription(
            TimetableRoute,
            Res.string.timetable,
            Icons.Outlined.DateRange,
        ),
        NavDescription(
            ModuleRoute,
            Res.string.modules,
            TablerIcons.Sailboat,
        ),
        NavDescription(
            SettingRoute,
            Res.string.settings,
            Icons.Outlined.Person,
        ),
    )

    NavigationSuiteScaffold(
        // 导航套件
        navigationItems = {
            NavigationSuiteItems(
                navDescriptions = navDescriptions,
                currentDestination = currentDestination,
                onNavigate = {
                    if (currentDestination?.hasRoute(it::class) == false) {
                        navController.apply {
                            currentDestination.route?.let {
                                popBackStack()
                            }
                            navigate(it)
                        }
                    }
                }
            )
        },
        // 套件类型
        navigationSuiteType = navigationSuiteType,
    ) {
        NavHost(
            navController = navController,
            startDestination = TimetableRoute,
        ) {
            // 课表
            timetableDestination()
            // 模块
            moduleDestination()
            // 设置
            settingDestination()
        }
    }
}

/**
 * 自定义 NavigationSuiteType。将 [NavigationBar]、
 * [ShortNavigationBarMedium] 和 [ShortNavigationBarCompact]
 * 统一为 [ShortNavigationBarCompact]。
 */
@Composable
private fun rememberNavigationSuiteType(): NavigationSuiteType {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val navigationSuiteType = remember(adaptiveInfo) {
        NavigationSuiteScaffoldDefaults
            .calculateFromAdaptiveInfo(adaptiveInfo)
    }

    return when (navigationSuiteType) {
        NavigationBar,
        ShortNavigationBarMedium,
        ShortNavigationBarCompact,
            -> ShortNavigationBarCompact

        else -> navigationSuiteType
    }
}

/**
 * [NavigationSuiteItem]s。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NavigationSuiteItems(
    navDescriptions: List<NavDescription>,
    currentDestination: NavDestination?,
    onNavigate: (route: Any) -> Unit,
) {
    navDescriptions.fastForEach { description ->
        NavigationSuiteItem(
            selected = currentDestination?.hierarchy?.any {
                it.hasRoute(description.route::class)
            } == true,
            onClick = { onNavigate(description.route) },
            icon = {
                Icon(
                    description.icon,
                    contentDescription = stringResource(description.name),
                )
            },
            label = { Text(stringResource(description.name)) },
        )
    }
}