package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.first
import org.kiteio.punica.AppVM
import org.kiteio.punica.ui.rememberRBlocking

/**
 * 课表时间线。
 *
 * @param spacing 元素间隙
 */
@Composable
fun TimetableTimeline(spacing: Dp, modifier: Modifier = Modifier) {
    val campus = rememberRBlocking { AppVM.campus.first() }

    Column(modifier = modifier) {
        for (index in 0..<6) {
            TimetableMealSpacer(index)

            TimelineCard(spacing = spacing, modifier = Modifier.weight(1f)) {
                val start = index * 2

                with(campus) {
                    // 节次
                    Text(
                        "${start + 1}-${start + 2}",
                        style = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
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


/**
 * 时间卡片。
 *
 * @param spacing 元素间隙
 */
@Composable
private fun TimelineCard(
    spacing: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize().padding(spacing),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    color = LocalContentColor.current.copy(0.6f),
                ),
                content = content,
            )
        }
    }
}