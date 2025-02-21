package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringArrayResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.wrapper.now
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.days_of_week

/**
 * 课表星期。
 *
 * @param currentPage 当前页码
 * @param spacing 元素间隙
 * @param timelineWeight 时间线权重
 * @param timelineMinWidth 时间线最小宽度
 */
@Composable
fun TimetableHeader(
    currentPage: Int,
    spacing: Dp,
    timelineWeight: Float,
    timelineMinWidth: Dp,
    modifier: Modifier = Modifier,
) {
    val now = remember { LocalDate.now() }
    val ordinal = now.dayOfWeek.ordinal

    val offset = currentPage - AppVM.week
    val offsetDate = now.plus(DatePeriod(days = offset * 7))

    val daysOfWeek = stringArrayResource(Res.array.days_of_week)

    Surface(shadowElevation = 1.dp) {
        Row(modifier = modifier) {
            Spacer(
                modifier = Modifier.widthIn(timelineMinWidth)
                    .fillMaxWidth(timelineWeight).padding(spacing)
            )

            daysOfWeek.forEachIndexed { index, dayOfWeek ->
                val date = offsetDate.plus(DatePeriod(days = index - ordinal))
                val isToday = offset == 0 && index == ordinal
                val fontWeight = if (isToday) FontWeight.Bold else LocalTextStyle.current.fontWeight

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.small,
                    color = if (isToday) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.surface,
                    contentColor = if (isToday) MaterialTheme.colorScheme.primary
                    else LocalContentColor.current
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // 星期
                        Text(dayOfWeek, fontWeight = fontWeight)
                        // 日期
                        Text(
                            "${date.monthNumber}-${date.dayOfMonth}",
                            fontWeight = fontWeight,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}