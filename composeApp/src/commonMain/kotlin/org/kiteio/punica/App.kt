package org.kiteio.punica

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.ui.Routes
import org.kiteio.punica.ui.page.ModulesPage
import org.kiteio.punica.ui.page.SettingsPage
import org.kiteio.punica.ui.page.TimetablePage
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.module
import punica.composeapp.generated.resources.settings
import punica.composeapp.generated.resources.timetable

@Composable
fun App(widthSizeClass: WindowWidthSizeClass) = MaterialTheme {
    val navController = rememberNavController()
    val routes = listOf(Routes.Timetable, Routes.Modules, Routes.Settings)

    var selectedIndex by remember { mutableIntStateOf(0) }

    NavBar(
        widthSizeClass > WindowWidthSizeClass.Compact,
        selectedIndex = selectedIndex,
        onItemClick = {
            // 导航目的地切换
            navController.navigate(routes[it])
            selectedIndex = it
        },
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.Timetable,
        ) {
            composable<Routes.Timetable> { TimetablePage() }
            composable<Routes.Modules> { ModulesPage() }
            composable<Routes.Settings> { SettingsPage() }
        }
    }
}


/**
 * 导航栏。
 */
@Composable
private fun NavBar(
    useNavRail: Boolean,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    content: @Composable () -> Unit,
) {
    val toggleNamedIcons = listOf(
        ToggleNamedIcon(Icons.Outlined.DateRange, Icons.Filled.DateRange, Res.string.timetable),
        ToggleNamedIcon(Icons.Outlined.AutoAwesomeMosaic, Icons.Filled.AutoAwesomeMosaic, Res.string.module),
        ToggleNamedIcon(Icons.Outlined.Settings, Icons.Filled.Settings, Res.string.settings),
    )

    Scaffold(
        bottomBar = {
            // 底部导航
            AnimatedVisibility(
                !useNavRail,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut(),
            ) {
                BottomBar(
                    selectedIndex = selectedIndex,
                    onItemClick = onItemClick,
                    toggleNamedIcons = toggleNamedIcons,
                )
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {
            // 侧边导航轨道
            AnimatedVisibility(
                useNavRail,
                enter = fadeIn() + slideInHorizontally(),
                exit = fadeOut(),
            ) {
                NavRail(
                    selectedIndex = selectedIndex,
                    onItemClick = onItemClick,
                    toggleNamedIcons = toggleNamedIcons,
                )
            }

            // 页面内容
            Surface(modifier = Modifier.weight(1f)) { content() }
        }
    }
}


/**
 * 底部导航。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomBar(
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    toggleNamedIcons: List<ToggleNamedIcon>,
) {
    ShortNavigationBar {
        toggleNamedIcons.forEachIndexed { index, toggleNameIcon ->
            ShortNavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemClick(index) },
                icon = {
                    ToggleIcon(
                        selectedIndex == index,
                        toggleNamedIcon = toggleNameIcon,
                    )
                },
                label = {
                    ToggleText(
                        selectedIndex == index,
                        text = stringResource(toggleNameIcon.nameRes),
                    )
                },
            )
        }
    }
}


/**
 * 侧边导航轨道。
 */
@Composable
private fun NavRail(
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    toggleNamedIcons: List<ToggleNamedIcon>,
) {
    NavigationRail {
        toggleNamedIcons.forEachIndexed { index, toggleNamedIcon ->
            NavigationRailItem(
                selected = selectedIndex == index,
                onClick = { onItemClick(index) },
                icon = {
                    ToggleIcon(
                        selectedIndex == index,
                        toggleNamedIcon = toggleNamedIcon,
                    )
                },
                label = {
                    ToggleText(
                        selectedIndex == index,
                        text = stringResource(toggleNamedIcon.nameRes),
                    )
                },
            )
        }
    }
}


/**
 * 可变换文字。
 */
@Composable
private fun ToggleText(checked: Boolean, text: String) = Text(
    text,
    color = if (checked) MaterialTheme.colorScheme.primary else Color.Unspecified,
)


/**
 * 可变换图标。
 */
@Composable
private fun ToggleIcon(checked: Boolean, toggleNamedIcon: ToggleNamedIcon) = Icon(
    if (checked) toggleNamedIcon.toggledIcon else toggleNamedIcon.icon,
    contentDescription = stringResource(toggleNamedIcon.nameRes),
    tint = if (checked) MaterialTheme.colorScheme.primary else LocalContentColor.current,
)


/**
 * 可变换命名图标。
 *
 * @property icon 变换前图标
 * @property toggledIcon 变换后图标
 * @property nameRes 名称字符串资源
 */
private data class ToggleNamedIcon(
    val icon: ImageVector,
    val toggledIcon: ImageVector,
    val nameRes: StringResource,
)