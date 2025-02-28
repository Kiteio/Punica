package org.kiteio.punica.ui.page.course

import org.kiteio.punica.client.course.CourseSystem
import org.kiteio.punica.client.course.api.SCourse
import org.kiteio.punica.client.course.api.SearchParameters
import org.kiteio.punica.client.course.api.search
import org.kiteio.punica.client.course.foundation.CourseCategory
import org.kiteio.punica.wrapper.IntPagingSource

class CoursesPagingSource(
    private val courseSystem: CourseSystem,
    val category: CourseCategory,
    private val searchParameters: SearchParameters,
) : IntPagingSource<SCourse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SCourse> {
        return try {
            val index = params.key ?: 0
            val courses = courseSystem.search(
                category,
                searchParameters,
                index,
                params.loadSize,
            )

            LoadResult.Page(
                data = courses,
                prevKey = params.key?.let { it - 1 },
                nextKey = index + 1,
            )
        } catch (throwable: Throwable) {
            LoadResult.Error(throwable)
        }
    }
}