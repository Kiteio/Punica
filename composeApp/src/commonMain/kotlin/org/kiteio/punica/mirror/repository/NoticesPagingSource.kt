package org.kiteio.punica.mirror.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import jakarta.inject.Singleton
import org.kiteio.punica.mirror.modal.notice.Notice
import org.kiteio.punica.mirror.service.NoticeService

/**
 * 教学通知分页源。
 *
 * @param service 教学通知服务
 */
@Singleton
class NoticesPagingSource(
    private val service: NoticeService,
) : PagingSource<Int, Notice>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notice> {
        return try {
            val page = params.key ?: 1
            val notices = service.getNotices(page)

            LoadResult.Page(
                data = notices,
                prevKey = params.key?.minus(1),
                nextKey = if (notices.size == params.loadSize) page + 1 else null,
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Notice>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}