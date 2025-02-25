package org.kiteio.punica.ui.page.notice

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig

private const val PAGE_SIZE = 14

class AcademicNoticeVM : ViewModel() {
    val noticePager = Pager(
        PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE),
    ) { NoticePagingSource() }.flow
}