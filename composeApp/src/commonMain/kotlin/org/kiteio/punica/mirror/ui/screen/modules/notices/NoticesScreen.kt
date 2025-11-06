package org.kiteio.punica.mirror.ui.screen.modules.notices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Bell
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.modal.notice.Notice
import org.kiteio.punica.mirror.ui.component.EmptyContent
import org.kiteio.punica.mirror.ui.component.ErrorContent
import org.kiteio.punica.mirror.ui.component.LoadingContent
import org.kiteio.punica.mirror.ui.component.NavBeforeIconButton
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import org.kiteio.punica.mirror.ui.navigation.navigate
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.academic_notices

/**
 * 教学通知页入口。
 */
fun EntryProviderScope<NavKey>.noticesEntry() {
    entry<NoticesRoute> { NoticesScreen() }
}

/**
 * 教学通知页路由。
 */
@Serializable
data object NoticesRoute : ModuleNavKey {
    override val strRes = Res.string.academic_notices
    override val icon = CssGgIcons.Bell
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun NoticesScreen() {
    val layoutDirection = LocalLayoutDirection.current

    val backStack = koinInject<NavBackStack<NavKey>>()

    val viewModel = koinViewModel<NoticesViewModel>()

    val pagingItems = viewModel.noticesPagerFlow.collectAsLazyPagingItems()
    val loadState = pagingItems.loadState.refresh

    val hazeState = rememberHazeState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.hazeEffect(
                    hazeState,
                    HazeMaterials.ultraThin(),
                ),
            )
        },
    ) { innerPadding ->
        when (loadState) {
            // 加载中
            LoadState.Loading -> LoadingContent(
                modifier = Modifier.padding(innerPadding),
            )

            // 加载失败
            is LoadState.Error -> ErrorContent(
                throwable = loadState.error,
                modifier = Modifier.padding(innerPadding),
            )

            // 非加载
            is LoadState.NotLoading -> {
                if (pagingItems.itemCount == 0) {
                    // 空白页
                    EmptyContent(modifier = Modifier.padding(innerPadding))
                } else {
                    // 通知内容
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(200.dp),
                        modifier = Modifier.padding(
                            start = innerPadding.calculateStartPadding(layoutDirection),
                            end = innerPadding.calculateEndPadding(layoutDirection),
                            bottom = innerPadding.calculateBottomPadding(),
                        ).hazeSource(hazeState),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp,
                        ),
                        verticalItemSpacing = 16.dp,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            // 顶部空白
                            Spacer(Modifier.height(innerPadding.calculateTopPadding()))
                        }
                        items(pagingItems.itemCount) {
                            // 通知
                            pagingItems[it]?.let { notice ->
                                NoticeCard(
                                    notice = notice,
                                    onClick = {
                                        val route = NoticeDetailRoute(notice.urlString)
                                        backStack.navigate(route)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 顶部导航栏。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(stringResource(NoticesRoute.strRes)) },
        modifier = modifier,
        navigationIcon = {
            NavBeforeIconButton()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
    )
}

/**
 * 通知卡片。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NoticeCard(notice: Notice, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.small,
    ) {
        ListItem(
            headlineContent = {
                // 标题
                Text(notice.title)
            },
            overlineContent = {
                // 发布时间
                Text(notice.time)
            },
            supportingContent = {
                // 网址
                Text(
                    notice.urlString,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelSmallEmphasized,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
        )
    }
}