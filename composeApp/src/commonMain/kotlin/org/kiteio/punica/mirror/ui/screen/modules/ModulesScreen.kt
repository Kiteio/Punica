package org.kiteio.punica.mirror.ui.screen.modules

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.ui.navigation.BottomNavKey
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import org.kiteio.punica.mirror.ui.navigation.navigate
import org.kiteio.punica.mirror.ui.screen.modules.calls.CallsRoute
import org.kiteio.punica.mirror.ui.screen.modules.cet.CetRoute
import org.kiteio.punica.mirror.ui.screen.modules.courses.CoursesRoute
import org.kiteio.punica.mirror.ui.screen.modules.coursesystem.CourseSystemRoute
import org.kiteio.punica.mirror.ui.screen.modules.exams.ExamsRoute
import org.kiteio.punica.mirror.ui.screen.modules.grades.GradesRoute
import org.kiteio.punica.mirror.ui.screen.modules.notices.NoticesRoute
import org.kiteio.punica.mirror.ui.screen.modules.plans.PlansRoute
import org.kiteio.punica.mirror.ui.screen.modules.progresses.ProgressesRoute
import org.kiteio.punica.mirror.ui.screen.modules.secondclass.SecondClassRoute
import org.kiteio.punica.mirror.ui.screen.modules.teachers.TeachersRoute
import org.kiteio.punica.mirror.ui.screen.modules.totp.TotpRoute
import org.kiteio.punica.mirror.ui.screen.modules.websites.WebsitesRoute
import org.koin.compose.koinInject
import punica.composeapp.generated.resources.*

/**
 * 模块页入口。
 */
fun EntryProviderScope<NavKey>.modulesEntry() {
    entry<ModulesRoute> { ModulesScreen() }
}

/**
 * 模块页路由。
 */
@Serializable
data object ModulesRoute : BottomNavKey {
    override val strRes = Res.string.modules
    override val icon = Icons.Outlined.AutoAwesomeMosaic
    override val selectedIcon = Icons.Filled.AutoAwesomeMosaic
}

@Composable
private fun ModulesScreen() {
    val backStack = koinInject<NavBackStack<NavKey>>()

    // 工具模块
    val toolModules = listOf(
        CallsRoute,
        TotpRoute,
        WebsitesRoute,
    )
    // 模块
    val modules = listOf(
        Module(TeachersRoute),
        Module(CoursesRoute),
        Module(PlansRoute),
        Module(CourseSystemRoute) {
            Text(stringResource(Res.string.not_yet_open))
        },
        Module(NoticesRoute) {
            Quantity(
                quantity = 0,
                noItemStrRes = Res.string.no_new_notice,
                pluralStrRes = Res.plurals.new_notice,
            )
        },
        Module(ExamsRoute) {
            Quantity(
                quantity = 0,
                noItemStrRes = Res.string.no_arrangement,
                pluralStrRes = Res.plurals.arrangement,
            )
        },
        Module(CetRoute) {
            Text(
                "2025.06.12",
                modifier = Modifier.basicMarquee(Int.MAX_VALUE),
            )
        },
        Module(GradesRoute) {
            Text(
                "移动应用开发",
                modifier = Modifier.basicMarquee(Int.MAX_VALUE),
            )
        },
        Module(SecondClassRoute) {
            ProgressIndicator(progress = { 0.5f })
        },
        Module(ProgressesRoute) {
            ProgressIndicator(progress = { 0.5f })
        },
    )

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // 工具模块
            LazyRow(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(toolModules, key = { it::class.simpleName!! }) {
                    ToolModule(
                        it,
                        onClick = {
                            backStack.navigate(it)
                        },
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .aspectRatio(1.1f)
                            .animateItem(),
                    )
                }
            }

            // 功能模块
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(144.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 8.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(modules, key = { it.route::class.simpleName!! }) {
                    Module(
                        it.route,
                        onClick = {
                            backStack.navigate(it.route)
                        },
                        supportingContent = it.supportingContent,
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
}

/**
 * 模块。
 *
 * @property route 路由
 */
data class Module(
    val route: ModuleNavKey,
    val supportingContent: (@Composable () -> Unit)? = null,
)

/**
 * 工具模块。
 */
@Composable
private fun ToolModule(
    route: ModuleNavKey,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
    ) {
        val name = stringResource(route.strRes)

        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                route.icon,
                contentDescription = name,
            )
            Text(
                name,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

/**
 * 模块。
 */
@Composable
private fun Module(
    route: ModuleNavKey,
    onClick: () -> Unit,
    supportingContent: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp,
    ) {
        ListItem(
            headlineContent = {
                // 模块名称
                Text(stringResource(route.strRes))
            },
            modifier = Modifier.clip(
                CutCornerShape(topStart = 16.dp),
            ),
            supportingContent = {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.labelSmall,
                ) {
                    supportingContent?.invoke()
                }
            },
            leadingContent = {
                // 图标
                Icon(
                    route.icon,
                    contentDescription = stringResource(route.strRes),
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        )
    }
}

/**
 * 数量。
 *
 * @param quantity 数量
 * @param noItemStrRes 数量为 0 时展示
 * @param pluralStrRes 数量不为 0 时展示
 */
@Composable
private fun Quantity(
    quantity: Int,
    noItemStrRes: StringResource,
    pluralStrRes: PluralStringResource,
) {
    val newMessagePlural = if (quantity == 0) {
        stringResource(noItemStrRes)
    } else {
        pluralStringResource(pluralStrRes, quantity)
    }

    Text(
        buildAnnotatedString {
            append(newMessagePlural)
            if (quantity > 0) {
                val startIndex = newMessagePlural.indexOf("$quantity")

                // 标记消息数量
                addStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.error,
                    ),
                    startIndex,
                    startIndex + "$quantity".length,
                )
            }
        }
    )
}

/**
 * 进度条。
 */
@Composable
private fun ProgressIndicator(
    progress: () -> Float,
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .height(2.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    )
}