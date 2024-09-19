package org.kiteio.punica.ui.screen.module

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import org.kiteio.punica.candy.launchCatch
import org.kiteio.punica.edu.EduNotice
import org.kiteio.punica.edu.Notice
import org.kiteio.punica.edu.NoticeItem
import org.kiteio.punica.openUri
import org.kiteio.punica.ui.component.BottomSheet
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.LazyPagingColumn
import org.kiteio.punica.ui.component.LinearProgressIndicator
import org.kiteio.punica.ui.component.MarkdownText
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.Pager
import org.kiteio.punica.ui.component.PagingSource
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.items
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route

/**
 * 教学通知
 */
@Composable
fun NoticeScreen() {
    // Pager 在切换页面时不能保留已加载内容，并且目前找不到解决方案
    val pager = remember { Pager(14) { NoticePagingSource() } }
    val noticeItems = pager.flow.collectAsLazyPagingItems()

    var noticeBottomSheetVisible by remember { mutableStateOf(false) }
    var visibleNoticeItem by remember { mutableStateOf<NoticeItem?>(null) }

    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.Notice) }) {
        LazyPagingColumn(
            loadState = noticeItems.loadState,
            contentPadding = PaddingValues(dp4(2))
        ) {
            items(noticeItems) {
                ElevatedCard(
                    onClick = {
                        visibleNoticeItem = it
                        noticeBottomSheetVisible = true
                    },
                    modifier = Modifier.padding(dp4(2))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dp4(4)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Title(text = it.title, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(dp4()))
                            SubduedText(text = it.time)
                        }
                        Spacer(modifier = Modifier.width(dp4()))

                        IconButton(onClick = { openUri(it.url) }) {
                            Icon(imageVector = Icons.Rounded.OpenInBrowser)
                        }
                    }
                }
            }
        }
    }

    NoticeBottomSheet(
        visible = noticeBottomSheetVisible,
        onDismiss = { noticeBottomSheetVisible = false },
        noticeItem = visibleNoticeItem
    )
}


/**
 * 教学通知 [PagingSource]
 */
private class NoticePagingSource : PagingSource<NoticeItem>() {
    override suspend fun loadCatching(params: LoadParams<Int>) =
        Page(EduNotice.list(params.key!!), params)
}


/**
 * 教学通知详情
 * @param visible
 * @param onDismiss
 * @param noticeItem
 */
@Composable
private fun NoticeBottomSheet(visible: Boolean, onDismiss: () -> Unit, noticeItem: NoticeItem?) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        var notice by remember { mutableStateOf<Notice?>(null) }

        LaunchedEffect(key1 = Unit) {
            launchCatch { noticeItem?.let { notice = EduNotice.notice(it) } }
        }

        notice?.run {
            if (pdf != null) PdfRendererViewCompose(url = pdf)
            else if (markdown != null) MarkdownText(
                markdown = markdown,
                contentPadding = PaddingValues(dp4(4))
            )
        } ?: LinearProgressIndicator()
    }
}