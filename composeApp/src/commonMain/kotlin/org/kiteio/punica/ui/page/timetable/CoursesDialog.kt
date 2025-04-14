package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.academic.foundation.ICourse
import org.kiteio.punica.ui.component.CardListItem
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.classroom
import punica.composeapp.generated.resources.close
import punica.composeapp.generated.resources.day_of_week
import punica.composeapp.generated.resources.days_of_week
import punica.composeapp.generated.resources.section_of
import punica.composeapp.generated.resources.teacher
import punica.composeapp.generated.resources.weeks

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
    CardListItem(
        headlineContent = {
            Text(course.name)
        },
        modifier = modifier,
        supportingContent = {
            Column {
                // 教师
                course.teacher?.let {
                    KV(stringResource(Res.string.teacher), it)
                }
                // 教室
                course.classroom?.let {
                    KV(stringResource(Res.string.classroom), it)
                }
                // 周次
                course.weeksString?.let {
                    KV(stringResource(Res.string.weeks), it)
                }

                Spacer(modifier = Modifier.height(8.dp))

                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.labelSmall,
                ) {
                    Row {
                        // 星期
                        course.dayOfWeek?.ordinal?.let {
                            Text(
                                buildString {
                                    append(stringResource(Res.string.day_of_week))
                                    append(stringArrayResource(Res.array.days_of_week)[it])
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        // 节次
                        course.sections?.run {
                            Text(
                                stringResource(
                                    Res.string.section_of,
                                    "${min()}-${max()}",
                                )
                            )
                        }
                    }
                }
            }
        },
    )
}


@Composable
private fun KV(key: String, value: String) {
    Row {
        Text(
            key,
            color = LocalContentColor.current.copy(0.8f),
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(value)
    }
}