package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.kiteio.punica.client.academic.foundation.ICourse
import org.kiteio.punica.ui.component.BorderStroke
import org.kiteio.punica.ui.component.PunicaCard

/**
 * 课表表格。
 *
 * @param currentPage 当前页码
 * @param courses 课程
 * @param spacing 元素间隙
 */
@Composable
fun TimetableTable(
    currentPage: Int,
    courses: List<List<ICourse>?>,
    onItemClick: (Int) -> Unit,
    spacing: Dp,
    modifier: Modifier = Modifier,
) {
    var weight = 1f

    HorizontalGrid(
        itemsInEachColumn = 6,
        lines = 7,
        modifier = modifier,
    ) { row, _, count ->
        val index = count + row
        // 过滤出当前周次的课程，若 week == null，则返回第一个课程
        val course = courses[index]?.firstOrNull {
            currentPage == 0 || it.weeks?.contains(currentPage) == true
        }

        // 通过节次数量或上一个课程的权重获取权重
        weight = course?.sections?.size?.toFloat()?.div(2) ?: when {
            row % 2 == 0 || weight == 1f -> 1f
            weight == 2f -> 0f
            else -> 0.5f // weight == 1.5
        }

        TimetableMealSpacer(row)

        Cell(
            course = course,
            spacing = spacing,
            onClick = { onItemClick(index) },
            modifier = Modifier.fillMaxWidth()
                .run { if (weight == 0f) height(0.dp) else weight(weight) }
                .padding(spacing),
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
    itemsInEachColumn: Int,
    lines: Int,
    modifier: Modifier = Modifier,
    itemContent: @Composable ColumnScope.(row: Int, column: Int, count: Int) -> Unit,
) {
    Row(modifier = modifier) {
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
 *
 * @param course 课程。若为 null 则显示空白，否则显示课程
 * @param spacing 元素间隙
 */
@Composable
private fun Cell(course: ICourse?, onClick: () -> Unit, spacing: Dp, modifier: Modifier = Modifier) {
    course?.run {
        PunicaCard(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.small,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            border = BorderStroke(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(spacing),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 课程名称
                Text(
                    name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                // 教室
                classroom?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        it.replace(Regex("[(（].*?[）)]"), ""),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    } ?: Spacer(modifier = modifier)
}