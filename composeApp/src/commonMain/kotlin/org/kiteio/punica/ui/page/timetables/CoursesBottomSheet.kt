package org.kiteio.punica.ui.page.timetables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.academic.foundation.CCourse
import org.kiteio.punica.ui.component.CardListItem
import org.kiteio.punica.ui.component.ModalBottomSheet
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.day_of_week
import punica.composeapp.generated.resources.days_of_week
import punica.composeapp.generated.resources.section_of

/**
 * 课程列表模态对话框。
 */
@Composable
fun CoursesBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    courses: List<CCourse>?,
) {
    ModalBottomSheet(visible, onDismissRequest = onDismissRequest) {
        LazyColumn(contentPadding = PaddingValues(8.dp)) {
            courses?.let {
                items(it) { course ->
                    Course(course, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}


/**
 * 课程。
 */
@Composable
private fun Course(course: CCourse, modifier: Modifier = Modifier) {
    CardListItem(
        // 名称
        headlineContent = { Text(course.name) },
        modifier = modifier,
        supportingContent = {
            Column {
                Text(
                    buildString {
                        // 周次
                        append(course.weeksString)
                        append("  ")
                        // 星期
                        append(stringResource(Res.string.day_of_week))
                        append(stringArrayResource(Res.array.days_of_week)[course.dayOfWeek.ordinal])
                        append("  ")
                        // 节次
                        append(stringResource(Res.string.section_of, course.sections.joinToString("-")))
                    }
                )
                // 教室
                course.classroom?.let {
                    Text(it)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                // 班级
                Text(course.clazz, style = MaterialTheme.typography.bodySmall)
            }
        },
        trailingContent = { course.teacher?.let { Text(it) } },
    )
}