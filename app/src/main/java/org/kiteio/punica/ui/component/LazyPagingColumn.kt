package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import org.kiteio.punica.candy.catch

/**
 * 分页 [LazyColumn]
 * @param loadState
 * @param modifier
 * @param contentPadding
 * @param content
 */
@Composable
fun LazyPagingColumn(
    loadState: CombinedLoadStates,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: LazyListScope.() -> Unit
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        content()

        if (loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading) item {
            LinearProgressIndicator()
        }
    }
}


/**
 * 适用于 [LazyPagingItems] 的 [items]
 * @receiver [LazyListScope]
 * @param lazyPagingItems
 * @param itemContent
 */
fun <T : Any> LazyListScope.items(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    items(lazyPagingItems.itemCount) { index ->
        lazyPagingItems[index]?.let { itemContent(it) }
    }
}


/**
 * [Pager]<[Int], [Value]>
 * @param pageSize
 * @param pagingSourceFactory
 * @return [Pager]<[Int], [Value]>
 */
fun <Value: Any> Pager(
    pageSize: Int,
    pagingSourceFactory: () -> PagingSource<Int, Value>
) = Pager(
    PagingConfig(pageSize, initialLoadSize = pageSize),
    initialKey = 0,
    pagingSourceFactory = pagingSourceFactory
)


/**
 * [PagingSource] 基类
 * @param Value : [Any]
 * @property initialKey
 */
abstract class PagingSource<Value : Any>(private val initialKey: Int = 0) : PagingSource<Int, Value>() {
    override fun getRefreshKey(state: PagingState<Int, Value>) =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.run {
                prevKey?.plus(1) ?: nextKey?.minus(1)
            }
        }


    override suspend fun load(params: LoadParams<Int>) = catch({ LoadResult.Error(it) }) { loadCatching(params) }


    /**
     * 被 try..catch 包围的 load
     * @param params
     * @return [PagingSource.LoadResult.Page]<[Int], [Value]>
     */
    abstract suspend fun loadCatching(params: LoadParams<Int>): LoadResult.Page<Int, Value>


    /**
     * 生成 [PagingSource.LoadResult.Page]
     * @param data
     * @param params
     * @return [PagingSource.LoadResult.Page]<[Int], [Value]>
     */
    fun Page(data: List<Value>, params: LoadParams<Int>) = LoadResult.Page(
        data,
        prevKey = if (params.key == initialKey) null else params.key?.minus(1),
        nextKey = if (data.isEmpty()) null else params.key?.plus(1)
    )
}