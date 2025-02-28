package org.kiteio.punica.ui.page.course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.kiteio.punica.client.course.CourseSystem
import org.kiteio.punica.client.course.api.WithdrawalLog
import org.kiteio.punica.client.course.api.getWithdrawalLogs
import org.kiteio.punica.ui.widget.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.widget.ModalBottomSheet
import org.kiteio.punica.wrapper.launchCatching

@Composable
fun LogsBottomSheet(visible: Boolean, onDismissRequest: () -> Unit, courseSystem: CourseSystem?) {
    ModalBottomSheet(visible, onDismissRequest) {
        var isLoading by remember { mutableStateOf(true) }
        val withdrawalLogs by produceState<List<WithdrawalLog>?>(null) {
            launchCatching {
                try {
                    value = courseSystem?.getWithdrawalLogs()
                } finally {
                    isLoading = false
                }
            }
        }

        LoadingNotNullOrEmpty(withdrawalLogs, isLoading = isLoading) { logs ->
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(200.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(logs) {
                    Card(modifier = Modifier.padding(4.dp)) {
                        ListItem(
                            headlineContent = { Text(it.courseName) },
                            trailingContent = {
                                Column {
                                    CompositionLocalProvider(
                                        LocalTextStyle provides MaterialTheme.typography.bodySmall,
                                    ) {
                                        Row {
                                            // 课程编号
                                            Text(it.courseId)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            // 学分
                                            Text("${it.credits}", fontWeight = FontWeight.Bold)
                                        }
                                        // 教师
                                        Text(it.courseTeacher)
                                        // 上课时间
                                        Text(it.time)
                                        Row {
                                            // 课程属性
                                            Text(it.courseCategory)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            // 选课分类
                                            Text(it.courseType)
                                        }
                                        Row {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            // 操作者
                                            Text(it.operator, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            // 操作说明
                                            Text(it.operator, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        // 操作时间
                                        Text(it.time)
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}