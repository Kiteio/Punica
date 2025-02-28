package org.kiteio.punica.wrapper

import androidx.paging.Pager
import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.cash.paging.PagingConfig

/**
 * [PagingSource]<[Int], [Value]>。
 */
abstract class IntPagingSource<Value : Any> : PagingSource<Int, Value>() {
    override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
        println(111111)
        println(state.anchorPosition)
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition)
        println(page)
        return page?.run { prevKey?.plus(1) ?: nextKey?.minus(1) }.also { println(it) }
    }
}


/**
 * 初始加载大小为 [pageSize] 的 [Pager]。
 */
fun <Value : Any> Pager(
    pageSize: Int,
    pagingSourceFactory: () -> IntPagingSource<Value>,
) = Pager(
    PagingConfig(
        pageSize = pageSize,
        initialLoadSize = pageSize,
    ),
    pagingSourceFactory = pagingSourceFactory,
)