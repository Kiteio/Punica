package org.kiteio.punica.ui.page.notice

import androidx.lifecycle.ViewModel
import org.kiteio.punica.wrapper.Pager

class NoticeVM : ViewModel() {
    val noticesPagerFlow = Pager(pageSize = 14) { NoticePagingSource() }.flow
}