package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.isoDayNumber
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.academic.api.Course
import punica.composeapp.generated.resources.*

/**
 * 课程对话框。
 *
 * @param courses 课程
 */
@Composable
fun CoursesDialog(visible: Boolean, onDismissRequest: () -> Unit, courses: List<Course>?) {
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
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(courses) {
                            Course(
                                course = it,
                                modifier = Modifier.padding(4.dp),
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
private fun Course(course: Course, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
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
                "${stringResource(Res.string.sections)}  ${course.sections.run { "${first()}-${last()}" }}"
            )
            // 星期
            Text("${stringResource(Res.string.day_of_week)}  ${course.dayOfWeek.isoDayNumber}")
        }
    }
}