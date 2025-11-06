package org.kiteio.punica.mirror.ui.screen.modules.notices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import org.kiteio.punica.mirror.repository.NoticesPagingSource
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class NoticesViewModel(
    pagingSource: NoticesPagingSource,
) : ViewModel() {
    val noticesPagerFlow = Pager(
        PagingConfig(
            pageSize = 14,
            initialLoadSize = 14,
        ),
    ) {
        pagingSource
    }.flow.cachedIn(viewModelScope)
}