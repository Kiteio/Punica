package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringArrayResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.ICourse
import org.kiteio.punica.ui.component.HorizontalPager
import org.kiteio.punica.ui.component.PunicaCard
import org.kiteio.punica.ui.compositionlocal.LocalWindowSizeClass
import org.kiteio.punica.ui.compositionlocal.isCompactWidth
import org.kiteio.punica.ui.compositionlocal.isMediumHeight
import org.kiteio.punica.ui.rememberRunBlocking
import org.kiteio.punica.wrapper.now
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.days_of_week

/** 元素外边距 */
private val margin = 0.8.dp

/** 时间线宽度 */
private val timelineWidth = 36.dp

/** 元素高度 */
private val itemHeight = 650.dp

/**
 * 课表。
 */
@Composable
fun Timetable(
    state: PagerState,
    week: Int,
    courses: List<List<ICourse>?>,
    onItemClick: (Int) -> Unit,
    note: String?,
    noteVisible: Boolean,
) {
    Column(modifier = Modifier.padding(margin)) {
        // 星期
        Header(
            week = week,
            currentWeekIndex = state.currentPage,
            dateVisible = true,
        )
        Spacer(modifier = Modifier.height(margin * 2))

        // 分页
        HorizontalPager(state, modifier = Modifier.weight(1f)) {
            // 课表
            Body(
                courses = courses,
                onItemClick = onItemClick,
                currentWeekIndex = state.currentPage,
            )
        }

        // 备注
        if (noteVisible && note != null) {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Text(
                    note,
                    modifier = Modifier.padding(margin),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}


/**
 * 无日期课表。
 */
@Composable
fun Timetable(
    courses: List<List<ICourse>?>,
    onItemClick: (Int) -> Unit,
) {
    Column(modifier = Modifier.padding(margin)) {
        // 星期
        Header(
            week = 0,
            currentWeekIndex = 0,
            dateVisible = false,
        )
        Spacer(modifier = Modifier.height(margin * 2))

        // 课表
        Body(
            courses = courses,
            onItemClick = onItemClick,
            currentWeekIndex = 0,
        )
    }
}


/**
 * 表头。
 */
@Composable
private fun Header(
    week: Int,
    currentWeekIndex: Int,
    dateVisible: Boolean,
) {
    val daysOfWeek = stringArrayResource(Res.array.days_of_week)

    Surface(shape = MaterialTheme.shapes.medium, shadowElevation = 1.dp) {
        Row {
            // 星期前空白
            Spacer(Modifier.width(timelineWidth).padding(margin))

            // 星期
            if (dateVisible) {
                val now = remember { LocalDate.now() }
                val ordinal = now.dayOfWeek.ordinal

                val offset = currentWeekIndex - week
                val offsetDate = remember(offset) { now.plus(DatePeriod(days = offset * 7)) }

                daysOfWeek.forEachIndexed { index, dayOfWeek ->
                    val date = offsetDate.plus(DatePeriod(days = index - ordinal))
                    val isToday = offset == 0 && index == ordinal

                    Surface(
                        onClick = {},
                        enabled = isToday,
                        modifier = Modifier.weight(1f).padding(margin),
                        shape = MaterialTheme.shapes.small,
                        color = if (isToday) MaterialTheme.colorScheme.surfaceContainerLowest else
                            MaterialTheme.colorScheme.surface,
                        shadowElevation = if (isToday) 2.dp else 0.dp,
                        contentColor = if (isToday) MaterialTheme.colorScheme.primary
                        else LocalContentColor.current.copy(0.4f)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 2.dp),
                        ) {
                            // 星期
                            Text(dayOfWeek, fontWeight = FontWeight.Bold)
                            // 日期
                            Text(
                                "${date.monthNumber}-${date.dayOfMonth}",
                                color = LocalTextStyle.current.color,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            } else {
                daysOfWeek.forEachIndexed { index, dayOfWeek ->
                    Surface(
                        modifier = Modifier.weight(1f).padding(margin),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 4.dp),
                        ) {
                            // 星期
                            Text(
                                dayOfWeek,
                                color = LocalContentColor.current.copy(0.6f),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * 内容。
 */
@Composable
private fun Body(courses: List<List<ICourse>?>, onItemClick: (Int) -> Unit, currentWeekIndex: Int) {
    val windowSizeClass = LocalWindowSizeClass.current

    LazyColumn {
        item {
            Row(
                modifier = Modifier.run {
                    if (
                        windowSizeClass.isCompactWidth ||
                        windowSizeClass.isMediumHeight
                    ) height(itemHeight)
                    else fillParentMaxHeight()
                },
            ) {
                // 时间线
                Timeline()

                // 表格
                Table(
                    currentWeekIndex = currentWeekIndex,
                    courses = courses,
                    onItemClick = onItemClick,
                )
            }
        }
    }
}


/**
 * 课表时间线。
 */
@Composable
private fun Timeline() {
    val campus = rememberRunBlocking { AppVM.campusFlow.first() }

    Column(modifier = Modifier.width(timelineWidth)) {
        for (index in 0..<6) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (index) {
                    2, 3 -> MaterialTheme.colorScheme.secondaryContainer.copy(0.2f)
                    else -> MaterialTheme.colorScheme.surface
                },
                modifier = Modifier.weight(1f).padding(margin),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                            color = LocalContentColor.current.copy(0.6f),
                        ),
                    ) {
                        val start = index * 2

                        with(campus) {
                            // 节次
                            Text(
                                "${start + 1}-${start + 2}",
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // 时间
                            Text(schedule[start].start)
                            Text(schedule[start].endInclusive)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(schedule[start + 1].start)
                            Text(schedule[start + 1].endInclusive)
                        }
                    }
                }
            }
        }
    }
}


/**
 * 课表表格。
 */
@Composable
private fun Table(
    courses: List<List<ICourse>?>,
    onItemClick: (Int) -> Unit,
    currentWeekIndex: Int,
) {
    var weight = 1f

    HorizontalGrid { row, _, count ->
        val index = count + row
        // 过滤出当前周次的课程，若 currentWeekIndex == 0，则返回第一个课程
        val course = courses[index]?.firstOrNull {
            currentWeekIndex == 0 || it.weeks?.contains(currentWeekIndex) == true
        }

        // 通过节次数量和上一个课程的权重获取权重
        weight = course?.sections?.size?.toFloat()?.div(2) ?: when {
            row % 2 == 0 || weight == 1f -> 1f
            weight == 2f -> 0f
            else -> 0.5f // weight == 1.5
        }

        Cell(
            course = course,
            onClick = { onItemClick(index) },
            modifier = Modifier.fillMaxWidth()
                .run { if (weight == 0f) height(0.dp) else weight(weight) }
                .padding(margin),
        )
    }
}


/**
 * 水平方向网格。
 *
 * @param itemsInEachColumn 每列数量
 * @param lines 行数
 */
@Composable
private fun HorizontalGrid(
    itemsInEachColumn: Int = 6,
    lines: Int = 7,
    itemContent: @Composable ColumnScope.(row: Int, column: Int, count: Int) -> Unit,
) {
    Row {
        for (column in 0..<lines) {
            val count = column * itemsInEachColumn
            Column(modifier = Modifier.weight(1f)) {
                for (row in 0..<itemsInEachColumn) {
                    itemContent(row, column, count)
                }
            }
        }
    }
}


/**
 * 课程格。
 */
@Composable
private fun Cell(course: ICourse?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    course?.run {
        PunicaCard(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.small,
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.4f),
            contentColor = MaterialTheme.colorScheme.primary,
            shadowElevation = 0.dp,
            border = BorderStroke(
                1.5.dp,
                MaterialTheme.colorScheme.surfaceVariant
            ),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(margin),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 课程名称
                Text(
                    name,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                )

                // 教室
                classroom?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        it.replace(Regex("[(（].*?[）)]"), ""),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    } ?: Spacer(modifier = modifier)
}