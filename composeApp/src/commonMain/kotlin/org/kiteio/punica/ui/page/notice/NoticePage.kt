package org.kiteio.punica.ui.page.notice

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cash.paging.compose.collectAsLazyPagingItems
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Bell
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.office.api.Notice
import org.kiteio.punica.ui.component.CardListItem
import org.kiteio.punica.ui.component.Loading
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.ui.page.modules.ModuleRoute
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.academic_notice

/**
 * 教学通知页面路由。
 */
@Serializable
object NoticeRoute : ModuleRoute {
    override val nameRes = Res.string.academic_notice
    override val icon = CssGgIcons.Bell
}


/**
 * 教学通知页面。
 */
@Composable
fun NoticePage() = viewModel { NoticeVM() }.Content()


@Composable
private fun NoticeVM.Content() {
    val notices = noticesPagerFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = { NavBackAppBar(title = { Text(stringResource(NoticeRoute.nameRes)) }) }
    ) { innerPadding ->
        Loading(notices, modifier = Modifier.padding(innerPadding)) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(232.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(notices.itemCount) { index ->
                    notices[index]?.let {
                        Notice(
                            notice = it,
                            modifier = Modifier.padding(8.dp),
                        )
                    }
                }
            }
        }
    }
}


/**
 * 通知。
 */
@Composable
private fun Notice(notice: Notice, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    CardListItem(
        headlineContent = { Text(notice.title) },
        onClick = { uriHandler.openUri(notice.urlString) },
        modifier = modifier,
        supportingContent = { Text(notice.time) },
    )
}