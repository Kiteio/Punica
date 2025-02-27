package org.kiteio.punica.ui.page.notice

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
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
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.ui.widget.Loading
import org.kiteio.punica.ui.widget.NavBackAppBar
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.academic_notice

/**
 * 教学通知页面路由。
 */
@Serializable
object AcademicNoticeRoute : ModuleRoute {
    override val nameRes = Res.string.academic_notice
    override val icon = CssGgIcons.Bell
}


/**
 * 教学通知页面。
 */
@Composable
fun AcademicNoticePage() = viewModel { AcademicNoticeVM() }.Content()


@Composable
private fun AcademicNoticeVM.Content() {
    val notices = noticesPagerFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = { NavBackAppBar(title = { Text(stringResource(AcademicNoticeRoute.nameRes)) }) }
    ) { innerPadding ->
        Loading(notices.loadState.refresh, modifier = Modifier.padding(innerPadding)) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(200.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(notices.itemCount) { index ->
                    notices[index]?.let {
                        Notice(
                            notice = it,
                            modifier = Modifier.padding(4.dp),
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

    ElevatedCard(onClick = { uriHandler.openUri(notice.urlString) }, modifier = modifier) {
        ListItem(
            headlineContent = { Text(notice.title) },
            supportingContent = { Text(notice.time) }
        )
    }
}