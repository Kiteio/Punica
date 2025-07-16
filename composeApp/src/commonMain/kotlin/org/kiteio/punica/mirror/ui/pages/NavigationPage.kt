package org.kiteio.punica.mirror.ui.pages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.Companion.NavigationBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.Companion.None
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
private fun PreviewNavigationPage() {
    PunicaExpressiveTheme {
        NavigationPage()
    }
}

/**
 * 导航页面。
 */
@Composable
private fun NavigationPage() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navDescriptions = listOf(
        // 课表
        NavDescription(
            TimetableRoute,
            Res.string.timetable,
            Icons.Outlined.DateRange,
        ),
        // 模块
        NavDescription(
            ModuleRoute,
            Res.string.modules,
            TablerIcons.Sailboat,
        ),
        // 设置
        NavDescription(
            SettingRoute,
            Res.string.settings,
            Icons.Outlined.Settings,
        ),
    )

    // 自定义导航套件，包含 HorizontalFloatingToolbar
    NavigationSuiteScaffold(
        navDescriptions = navDescriptions,
        currentDestination = currentDestination,
        onNavigate = {
            // 只在导航目的地不为当前目的地时导航
            if (currentDestination?.hasRoute(it::class) == false) {
                navController.apply {
                    // 弹出上一个目的地
                    currentDestination.route?.let {
                        popBackStack()
                    }
                    navigate(it)
                }
            }
        },
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
 * 导航套件脚手架。
 *
 * @param navDescriptions 导航描述
 * @param currentDestination 当前目的地
 * @param onNavigate 导航事件
 */
@Composable
private fun NavigationSuiteScaffold(
    navDescriptions: List<NavDescription>,
    currentDestination: NavDestination?,
    onNavigate: (route: Any) -> Unit,
    content: @Composable () -> Unit,
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val navigationSuiteType = remember(adaptiveInfo) {
        NavigationSuiteScaffoldDefaults
            .calculateFromAdaptiveInfo(adaptiveInfo)
            // 将 NavigationBar 替换为 None 以
            // 显示 HorizontalFloatingToolbar
            .takeIf { it != NavigationBar } ?: None
    }

    NavigationSuiteScaffold(
        // 导航套件
        navigationItems = {
            NavigationSuiteItems(
                navDescriptions = navDescriptions,
                currentDestination = currentDestination,
                onNavigate = onNavigate,
            )
        },
        // 套件类型
        navigationSuiteType = navigationSuiteType,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
            if (navigationSuiteType == None) {
                HorizontalFloatingToolbar(
                    navDescriptions = navDescriptions,
                    currentDestination = currentDestination,
                    onNavigate = onNavigate,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}


/**
 * [NavigationSuiteItem]s。
 *
 * @param navDescriptions 导航描述
 * @param currentDestination 当前目的地
 * @param onNavigate 导航事件
 */
@Composable
private fun NavigationSuiteItems(
    navDescriptions: List<NavDescription>,
    currentDestination: NavDestination?,
    onNavigate: (route: Any) -> Unit,
) {
    navDescriptions.fastForEach { description ->
        val name = stringResource(description.name)

        NavigationSuiteItem(
            selected = currentDestination?.hierarchy?.any {
                it.hasRoute(description.route::class)
            } == true,
            onClick = { onNavigate(description.route) },
            icon = {
                Icon(
                    description.icon,
                    contentDescription = name,
                )
            },
            label = { Text(name) },
        )
    }
}

/**
 * [HorizontalFloatingToolbar]。
 *
 * @param navDescriptions 导航描述
 * @param currentDestination 当前目的地
 * @param onNavigate 导航事件
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HorizontalFloatingToolbar(
    navDescriptions: List<NavDescription>,
    currentDestination: NavDestination?,
    onNavigate: (route: Any) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalFloatingToolbar(
        expanded = true,
        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
            toolbarContainerColor = MaterialTheme
                .colorScheme
                .surfaceContainer.copy(.3f),
        ),
        modifier = modifier.offset(y = -ScreenOffset)
            .border(
                1.dp,
                MaterialTheme.colorScheme.surfaceVariant.copy(.5f),
                FloatingToolbarDefaults.ContainerShape,
            ),
    ) {
        navDescriptions.forEach { description ->
            HorizontalFloatingToolbarItem(
                selected = currentDestination?.hasRoute(
                    description.route::class,
                ) == true,
                onClick = { onNavigate(description.route) },
                icon = description.icon,
                label = stringResource(description.name),
            )
        }
    }
}

/**
 * [HorizontalFloatingToolbar] 项。
 *
 * @param selected 是否选中
 * @param icon 图标
 * @param label 标签
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HorizontalFloatingToolbarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
) {
    ToggleButton(
        selected,
        onCheckedChange = { onClick() },
        modifier = Modifier.padding(horizontal = 4.dp),
        shapes = ToggleButtonDefaults.shapes(
            pressedShape = FloatingToolbarDefaults.ContainerShape,
            checkedShape = FloatingToolbarDefaults.ContainerShape,
        ),
    ) {
        Icon(icon, contentDescription = label)
        Spacer(Modifier.width(4.dp))
        Text(label)
    }
}