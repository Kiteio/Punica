package org.kiteio.punica.mirror.ui.screen.modules.notices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.ui.component.ErrorContent
import org.kiteio.punica.mirror.ui.component.LoadingContent
import org.kiteio.punica.mirror.ui.component.NavBeforeIconButton
import org.koin.compose.viewmodel.koinViewModel
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.open_in_browser

/**
 * 教学通知详情页入口。
 */
fun EntryProviderScope<NavKey>.noticeDetailScreen() {
    entry<NoticeDetailRoute> {
        NoticeDetailScreen(urlString = it.urlString)
    }
}

/**
 * 教学通知详情页路由。
 */
@Serializable
data class NoticeDetailRoute(
    val urlString: String,
) : NavKey

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun NoticeDetailScreen(urlString: String) {
    val uriHandler = LocalUriHandler.current
    val layoutDirection = LocalLayoutDirection.current

    val viewModel = koinViewModel<NoticeDetailViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val hazeState = rememberHazeState()

    LaunchedEffect(Unit) {
        viewModel.dispatch(NoticeDetailIntent.Load(urlString))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                onOpenInBrowser = {
                    uriHandler.openUri(urlString)
                },
                modifier = Modifier.hazeEffect(
                    hazeState,
                    HazeMaterials.ultraThin(),
                ),
            )
        },
    ) { innerPadding ->
        when (uiState) {
            // 加载中
            NoticeDetailUiState.Loading -> LoadingContent(
                modifier = Modifier.padding(innerPadding),
            )

            // 加载失败
            is NoticeDetailUiState.Error -> ErrorContent(
                throwable = (uiState as NoticeDetailUiState.Error).e,
                modifier = Modifier.padding(innerPadding),
            )

            // 加载成功
            is NoticeDetailUiState.Success -> {
                val htmlString = (uiState as NoticeDetailUiState.Success).noticeHtml
                val html = remember(htmlString) {
                    htmlToAnnotatedString(htmlString)
                }

                LazyColumn(
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        Spacer(Modifier.height(innerPadding.calculateTopPadding()))
                    }
                    item {
                        Text(html)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    onOpenInBrowser: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {},
        modifier = modifier,
        navigationIcon = {
            NavBeforeIconButton()
        },
        actions = {
            val openInBrowser = stringResource(Res.string.open_in_browser)
            AppBarRow {
                clickableItem(
                    onClick = onOpenInBrowser,
                    icon = {
                        Icon(
                            Icons.Outlined.OpenInBrowser,
                            contentDescription = openInBrowser,
                        )
                    },
                    label = openInBrowser,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
    )
}