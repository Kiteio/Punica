package org.kiteio.punica.ui.page.notice

import org.kiteio.punica.client.office.AcademicOffice
import org.kiteio.punica.client.office.api.Notice
import org.kiteio.punica.client.office.api.getNotices
import org.kiteio.punica.wrapper.IntPagingSource

class NoticePagingSource : IntPagingSource<Notice>() {
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
        } catch (throwable: Throwable) {
            LoadResult.Error(throwable)
        }
    }
}