package org.kiteio.punica.ui.page.notice

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.kiteio.punica.client.office.AcademicOffice
import org.kiteio.punica.client.office.api.Notice
import org.kiteio.punica.client.office.api.getNotices

class NoticePagingSource : PagingSource<Int, Notice>() {
    private val academicOffice = AcademicOffice()


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notice> {
        return try {
            val index = params.key ?: 1
            val notices = academicOffice.getNotices(index)

            LoadResult.Page(
                data = notices,
                prevKey = params.key?.let { it - 1 },
                nextKey = index + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, Notice>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition)
        return page?.run { prevKey?.plus(1) ?: nextKey?.minus(1) }
    }
}