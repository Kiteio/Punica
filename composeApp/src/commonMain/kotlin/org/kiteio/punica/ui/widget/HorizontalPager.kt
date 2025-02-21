package org.kiteio.punica.ui.widget

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * [HorizontalPager]。
 */
@Composable
fun HorizontalPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    pageContent: @Composable (PagerScope.(page: Int) -> Unit)
) {
    HorizontalPager(
        state = state,
        modifier = modifier,
        // 额外加载1页
        beyondViewportPageCount = 1,
        verticalAlignment = Alignment.Top,
        pageContent = pageContent,
    )
}