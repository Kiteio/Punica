package org.kiteio.punica.ui.page.course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
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
import org.kiteio.punica.ui.component.HorizontalTabPager
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.ModalBottomSheet
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.ui.compositionlocal.LocalWindowSizeClass
import org.kiteio.punica.ui.compositionlocal.isCompactWidth
import org.kiteio.punica.ui.compositionlocal.isMediumHeight
import org.kiteio.punica.ui.page.account.DeleteDialog
import org.kiteio.punica.ui.page.timetable.TimetableHeader
import org.kiteio.punica.ui.page.timetable.TimetableTable
import org.kiteio.punica.ui.page.timetable.TimetableTimeline
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
                tabContent = { Text(stringResource(tabs[it])) }
            ) { page ->
                when (page) {
                    0 -> List(
                        courses,
                        courseSystem = courseSystem,
                        onWitidrawCourse = { flag = !flag }
                    )

                    1 -> Timetable(courses)
                }
            }
        }
    }
}


@Composable
private fun List(courses: List<MCourse>, courseSystem: CourseSystem?, onWitidrawCourse: () -> Unit) {
    val scope = rememberCoroutineScope()
    var deleteDialogVisible by remember { mutableStateOf(false) }
    var mCourse by remember { mutableStateOf<MCourse?>(null) }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(256.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(courses) {
            Card(modifier = Modifier.padding(4.dp)) {
                ListItem(
                    headlineContent = { Text(it.name) },
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
                                }
                                // 教师
                                Text(it.teacher)
                                // 上课时间
                                it.time?.let { text -> Text(text) }
                                // 上课地点
                                it.classroom?.let { text -> Text(text) }
                                // 课程属性
                                Text(it.category)
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
                    }
                )
            }
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
                    courseSystem?.run {
                        delete(id)
                        onWitidrawCourse()
                        showToast(getString(Res.string.withdraw_course_successful))
                        deleteDialogVisible = false
                        mCourse = null
                    }
                }
            }
        },
    )
}


@Composable
private fun Timetable(courses: List<MCourse>) {
    var timetable by remember { mutableStateOf<List<List<MCourse>?>?>(null) }

    LaunchedEffectCatching(Unit) {
        // 生成课表
        val list = MutableList<MutableList<MCourse>?>(42) { null }
        courses.forEach {
            if (it.dayOfWeek != null && it.sections != null) {
                val index = it.dayOfWeek.ordinal * 6 + (it.sections.minOf { it } - 1) / 2

                (list[index] ?: mutableListOf<MCourse>().also { list[index] = it }).add(it)
            }
        }
        timetable = list
    }

    LoadingNotNullOrEmpty(
        timetable,
        isLoading = timetable == null,
    ) {
        val windowSizeClass = LocalWindowSizeClass.current
        val spacing = 2.dp
        val lineHeight = 600.dp
        val timelineWeight = 0.05f
        val timelineMinWidth = 32.dp
        val week = 0

        Column {
            // 星期
            TimetableHeader(
                week = week,
                currentPage = week,
                spacing = spacing,
                timelineWeight = timelineWeight,
                timelineMinWidth = timelineMinWidth,
                modifier = Modifier.padding(spacing),
            )
            // 时间线和表格
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(spacing),
            ) {
                item {
                    Row(
                        modifier = Modifier.run {
                            if (
                                windowSizeClass.isCompactWidth ||
                                windowSizeClass.isMediumHeight
                            ) height(lineHeight)
                            else fillParentMaxHeight()
                        },
                    ) {
                        // 时间线
                        TimetableTimeline(
                            spacing = spacing,
                            modifier = Modifier.widthIn(timelineMinWidth)
                                .fillMaxWidth(timelineWeight),
                        )
                        // 课表
                        TimetableTable(
                            currentPage = week,
                            courses = it,
                            onItemClick = {},
                            spacing = spacing,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}