package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.kiteio.punica.wrapper.launchCatching

/**
 * [PrimaryTabRow] + [HorizontalPager]。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorizontalTabPager(
    state: PagerState,
    tabContent: @Composable (Int) -> Unit,
    tabScrollable: Boolean = false,
    modifier: Modifier = Modifier,
    pageContent: @Composable PagerScope.(Int) -> Unit,
) {
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        // TabRow
        Surface(shadowElevation = 1.dp) {
            TabRow(
                selectedTabIndex = state.currentPage,
                indicator = {
                    // 底部指示设置为圆形短线
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(state.currentPage),
                        width = 24.dp,
                        height = 3.dp,
                        shape = CircleShape,
                    )
                },
                scrollable = tabScrollable,
            ) {
                // Tabs
                repeat(state.pageCount) {
                    Tab(
                        selected = state.currentPage == it,
                        onClick = { scope.launchCatching { state.animateScrollToPage(it) } },
                    ) {
                        Box(modifier = Modifier.padding(vertical = 8.dp)) { tabContent(it) }
                    }
                }
            }
        }

        // Pager
        HorizontalPager(
            state = state,
            pageContent = pageContent,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabRow(
    selectedTabIndex: Int,
    indicator: @Composable TabIndicatorScope.() -> Unit = {},
    scrollable: Boolean,
    tabs: @Composable () -> Unit,
) {
    if (scrollable) {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = indicator,
            divider = {},
            tabs = tabs,
        )
    } else {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = indicator,
            divider = {},
            tabs = tabs,
        )
    }
}