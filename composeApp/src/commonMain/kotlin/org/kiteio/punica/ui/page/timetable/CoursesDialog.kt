package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.academic.foundation.ICourse
import org.kiteio.punica.ui.component.PunicaCard
import punica.composeapp.generated.resources.*

/**
 * 课程对话框。
 *
 * @param courses 课程
 */
@Composable
fun CoursesDialog(visible: Boolean, onDismissRequest: () -> Unit, courses: List<ICourse>?) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            // 关闭
            confirmButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(Res.string.close))
                }
            },
            text = {
                if (courses != null) {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(courses) {
                            Course(
                                course = it,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                    }
                }
            }
        )
    }
}


/**
 * 课程。
 */
@Composable
private fun Course(course: ICourse, modifier: Modifier = Modifier) {
    PunicaCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // 课程名称
            Text(
                course.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
            )
            // 教师
            course.teacher?.let {
                Text("${stringResource(Res.string.teacher)}  $it")
            }
            // 教室
            Text("${stringResource(Res.string.classroom)}  ${course.classroom}")
            // 周次
            Text("${stringResource(Res.string.weeks)}  ${course.weeksString}")
            // 节次
            Text(
                buildString {
                    append(stringResource(Res.string.sections))
                    append("  ")
                    append(course.sections?.run { "${min()}-${max()}" } ?: "")
                    println(course.sections)
                },
            )
            // 星期
            course.dayOfWeek?.ordinal?.let {
                Text(
                    buildString {
                        append(stringResource(Res.string.day_of_week))
                        append("  ")
                        append(stringArrayResource(Res.array.days_of_week)[it])
                    },
                )
            }
        }
    }
}