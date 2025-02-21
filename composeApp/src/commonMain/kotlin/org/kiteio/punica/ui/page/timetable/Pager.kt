package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.kiteio.punica.client.academic.api.Course
import org.kiteio.punica.ui.compositionlocal.LocalWindowSizeClass
import org.kiteio.punica.ui.compositionlocal.isCompactWidth
import org.kiteio.punica.ui.compositionlocal.isMediumHeight
import org.kiteio.punica.ui.widget.HorizontalPager

/**
 * 课表分页。
 *
 * @param courses 课程
 * @param lineHeight 行高
 * @param spacing 元素间隙
 * @param timelineWeight 时间线权重
 * @param timelineMinWidth 时间线最小宽度
 */
@Composable
fun TimetablePager(
    state: PagerState,
    courses: List<List<Course>?>,
    onItemClick: (Int) -> Unit,
    lineHeight: Dp,
    spacing: Dp,
    timelineWeight: Float,
    timelineMinWidth: Dp,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = LocalWindowSizeClass.current

    HorizontalPager(state = state, modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(spacing),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Row(
                    modifier = Modifier.run {
                        if (
                            windowSizeClass.isCompactWidth ||
                            windowSizeClass.isMediumHeight
                        ) height(lineHeight)
                        else fillParentMaxHeight()
                    },
                ) {
                    // 时间线
                    TimetableTimeline(
                        spacing = spacing,
                        modifier = Modifier.widthIn(timelineMinWidth)
                            .fillMaxWidth(timelineWeight),
                    )
                    // 课表
                    TimetableTable(
                        currentPage = it,
                        courses = courses,
                        onItemClick = onItemClick,
                        spacing = spacing,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}