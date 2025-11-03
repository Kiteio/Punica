package org.kiteio.punica.mirror.ui.screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.Companion.NavigationBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.Companion.None
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.ui.navigation.BottomNavKey
import org.kiteio.punica.mirror.ui.navigation.polymorphic
import org.kiteio.punica.mirror.ui.navigation.resetTo
import org.kiteio.punica.mirror.ui.screen.me.MeRoute
import org.kiteio.punica.mirror.ui.screen.me.meEntry
import org.kiteio.punica.mirror.ui.screen.modules.ModulesRoute
import org.kiteio.punica.mirror.ui.screen.modules.modulesEntry
import org.kiteio.punica.mirror.ui.screen.photo.PhotoRoute
import org.kiteio.punica.mirror.ui.screen.photo.photoEntry
import org.kiteio.punica.mirror.ui.screen.timetable.TimetableRoute
import org.kiteio.punica.mirror.ui.screen.timetable.timetableEntry

/**
 * 主页入口。
 */
fun EntryProviderScope<NavKey>.mainEntry() {
    entry<MainRoute> { MainScreen() }
}

/**
 * 主页路由。
 */
@Serializable
data object MainRoute : NavKey

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MainScreen() {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(TimetableRoute.serializer())
                polymorphic(PhotoRoute.serializer())
                polymorphic(ModulesRoute.serializer())
                polymorphic(MeRoute.serializer())
            }
        },
        TimetableRoute,
    )

    // 路由列表
    val routes = listOf(
        TimetableRoute,
        // GalleryRoute,
        ModulesRoute,
        MeRoute,
    )

    val adaptiveInfo = currentWindowAdaptiveInfo()
    // 导航栏布局类型
    val layoutType = remember(adaptiveInfo) {
        NavigationSuiteScaffoldDefaults
            .calculateFromAdaptiveInfo(adaptiveInfo)
            // 将 NavigationBar 替换为 None 以
            // 显示 HorizontalFloatingToolbar
            .takeIf { it != NavigationBar } ?: None
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            // 多个自适应导航项
            navigationSuiteItems(
                routes = routes,
                currentRoute = backStack.last(),
                onNavigate = backStack::resetTo,
            )
        },
        layoutType = layoutType,
    ) {
        Scaffold { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                NavDisplay(
                    backStack = backStack,
                    entryProvider = entryProvider {
                        // 课表页
                        timetableEntry()
                        // 相册页
                        photoEntry()
                        // 模块页
                        modulesEntry()
                        // 我的页
                        meEntry()
                    },
                )

                if (layoutType == None) {
                    // 悬浮工具栏
                    HorizontalFloatingToolbar(
                        routes = routes,
                        currentRoute = backStack.last(),
                        onNavigate = backStack::resetTo,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(innerPadding)
                            .offset(y = -FloatingToolbarDefaults.ScreenOffset),
                    )
                }
            }
        }
    }
}

/**
 * 多个自适应导航项。
 *
 * @param routes 路由列表
 * @param currentRoute 当前路由
 */
private fun NavigationSuiteScope.navigationSuiteItems(
    routes: List<BottomNavKey>,
    currentRoute: NavKey,
    onNavigate: (route: BottomNavKey) -> Unit,
) {
    routes.forEach {
        val selected = it == currentRoute

        item(
            selected = selected,
            onClick = { onNavigate(it) },
            icon = {
                SelectableIcon(
                    selected = selected,
                    icon = it.icon,
                    selectedIcon = it.selectedIcon,
                    contentDescription = stringResource(it.strRes),
                )
            },
            label = {
                Text(
                    stringResource(it.strRes),
                    color = LocalContentColor.current.run {
                        if (selected) this else copy(0.8f)
                    },
                )
            },
        )
    }
}

/**
 * 悬浮工具栏。
 *
 * @param routes 路由列表
 * @param currentRoute 当前路由
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HorizontalFloatingToolbar(
    routes: List<BottomNavKey>,
    currentRoute: NavKey,
    onNavigate: (route: BottomNavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier,
        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
            toolbarContainerColor = MaterialTheme.colorScheme
                .primaryContainer.copy(0.8f),
        ),
        expandedShadowElevation = 2.dp,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            routes.forEach { route ->
                val selected = route == currentRoute

                ElevatedToggleButton(
                    checked = selected,
                    onCheckedChange = {
                        onNavigate(route)
                    },
                ) {
                    SelectableIcon(
                        selected = selected,
                        icon = route.icon,
                        selectedIcon = route.selectedIcon,
                        contentDescription = stringResource(route.strRes),
                    )
                }
            }
        }
    }
}

/**
 * 可切换选中状态的图标。
 *
 * @param selected 是否选中
 * @param icon 未选中图标
 * @param selectedIcon 选中图标
 */
@Composable
private fun SelectableIcon(
    selected: Boolean,
    icon: ImageVector,
    selectedIcon: ImageVector,
    contentDescription: String,
) {
    Icon(
        if (selected) selectedIcon else icon,
        contentDescription = contentDescription,
        tint = LocalContentColor.current.run {
            if (selected) this else copy(0.8f)
        },
    )
}