package org.kiteio.punica.ui.page.course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.academic.foundation.MCourse
import org.kiteio.punica.client.course.CourseSystem
import org.kiteio.punica.client.course.api.delete
import org.kiteio.punica.client.course.api.getCourses
import org.kiteio.punica.ui.component.*
import org.kiteio.punica.ui.page.timetable.Timetable
import org.kiteio.punica.ui.page.totp.DeleteDialog
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.*

@Composable
fun MyCoursesBottomSheet(visible: Boolean, onDismissRequest: () -> Unit, courseSystem: CourseSystem?) {
    ModalBottomSheet(visible, onDismissRequest) {
        var isLoading by remember { mutableStateOf(true) }
        var flag by remember { mutableStateOf(false) }
        val mCourses by produceState<List<MCourse>?>(null, flag) {
            launchCatching {
                try {
                    value = courseSystem?.getCourses()
                } finally {
                    isLoading = false
                }
            }
        }

        LoadingNotNullOrEmpty(mCourses, isLoading = isLoading) { courses ->
            val tabs = listOf(Res.string.selected_courses, Res.string.timetable)
            val state = rememberPagerState { tabs.size }

            HorizontalTabPager(
                state,
                tabContent = { Text(stringResource(tabs[it])) },
            ) { page ->
                when (page) {
                    0 -> List(
                        courses,
                        courseSystem = courseSystem,
                        onWithdrawCourse = { flag = !flag }
                    )

                    1 -> {
                        var timetable by remember { mutableStateOf<List<List<MCourse>?>?>(null) }

                        LaunchedEffectCatching(Unit) {
                            // 生成课表
                            val list = MutableList<MutableList<MCourse>?>(42) { null }
                            courses.forEach { course ->
                                if (course.dayOfWeek != null && course.sections != null) {
                                    // 计算当前课程下标
                                    val index = course.dayOfWeek.ordinal * 6 + (course.sections.minOf { it } - 1) / 2

                                    (list[index] ?: mutableListOf<MCourse>().also { list[index] = it }).add(course)
                                }
                            }
                            timetable = list
                        }

                        LoadingNotNullOrEmpty(
                            timetable,
                            isLoading = timetable == null,
                        ) {
                            Timetable(it, onItemClick = {})
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun List(courses: List<MCourse>, courseSystem: CourseSystem?, onWithdrawCourse: () -> Unit) {
    val scope = rememberCoroutineScope()
    var deleteDialogVisible by remember { mutableStateOf(false) }
    var mCourse by remember { mutableStateOf<MCourse?>(null) }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(256.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(courses) {
            CardListItem(
                headlineContent = { Text(it.name) },
                modifier = Modifier.padding(8.dp),
                supportingContent = {
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
                                Spacer(modifier = Modifier.width(8.dp))
                                // 课程属性
                                Text(it.category)
                            }
                            // 教师
                            Text(it.teacher)
                            // 上课时间
                            it.time?.let { text -> Text(text) }
                            // 上课地点
                            it.classroom?.let { text -> Text(text) }
                        }
                    }
                },
                trailingContent = {
                    TextButton(
                        onClick = {
                            mCourse = it
                            deleteDialogVisible = true
                        }
                    ) {
                        Text(
                            stringResource(Res.string.withdraw_course),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )
        }
    }

    DeleteDialog(
        deleteDialogVisible,
        onDismissRequest = {
            deleteDialogVisible = false
            mCourse = null
        },
        onConfirm = {
            // 退课
            scope.launchCatching {
                mCourse?.run {
                    courseSystem?.let {
                        it.delete(id)
                        onWithdrawCourse()
                        showToast(getString(Res.string.withdraw_course_successful))
                        deleteDialogVisible = false
                        mCourse = null
                    }
                }
            }
        },
    )
}