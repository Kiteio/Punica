package org.kiteio.punica.ui.page.course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import com.github.androidpasswordstore.sublimefuzzy.Fuzzy
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.client.course.CourseSystem
import org.kiteio.punica.client.course.api.SCourse
import org.kiteio.punica.client.course.api.SearchParameters
import org.kiteio.punica.client.course.api.delete
import org.kiteio.punica.client.course.api.select
import org.kiteio.punica.client.course.foundation.CourseCategory
import org.kiteio.punica.ui.component.CardListItem
import org.kiteio.punica.ui.component.Loading
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.ui.page.account.DeleteDialog
import org.kiteio.punica.wrapper.Pager
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.*

/**
 * 课程。
 */
@Composable
fun Courses(courseSystem: CourseSystem, category: CourseCategory, query: String) {
    val scope = rememberCoroutineScope()

    var parameters by remember { mutableStateOf(SearchParameters.Empty) }
    val courses = remember(parameters) {
        Pager(pageSize = 15) {
            CoursesPagingSource(courseSystem, category, parameters)
        }.flow
    }.collectAsLazyPagingItems()

    var deleteDialogVisible by remember { mutableStateOf(false) }
    var sCourse by remember { mutableStateOf<SCourse?>(null) }

    var filterBottomSheetVisible by remember { mutableStateOf(false) }

    Column {
        if (
            category != CourseCategory.BASIC &&
            category != CourseCategory.OPTIONAL
        ) {
            // 过滤
            CoursesFilter(
                parameters,
                onParamsChange = { parameters = it },
                onOpenFilter = { filterBottomSheetVisible = true },
            )
        }
        // 课程
        Loading(courses) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(280.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(courses.itemCount) {
                    courses[it]?.let { course ->
                        if (
                            query.isBlank() ||
                            Fuzzy.fuzzyMatchSimple(query, course.name) ||
                            Fuzzy.fuzzyMatchSimple(query, course.teacher) ||
                            course.classroom?.let { classroom -> Fuzzy.fuzzyMatchSimple(query, classroom) } == true ||
                            Fuzzy.fuzzyMatchSimple(query, course.courseProvider)
                        ) {
                            Course(
                                course,
                                onSelect = {
                                    // 选课
                                    scope.launchCatching {
                                        courseSystem.select(course.id, category, null)
                                        courses.refresh()
                                        showToast(getString(Res.string.select_course_successful))
                                    }
                                },
                                onWithdraw = {
                                    sCourse = course
                                    deleteDialogVisible = true
                                },
                                modifier = Modifier.padding(8.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    DeleteDialog(
        deleteDialogVisible,
        onDismissRequest = {
            deleteDialogVisible = false
            sCourse = null
        },
        onConfirm = {
            // 退课
            scope.launchCatching {
                sCourse?.run {
                    courseSystem.delete(id)
                    courses.refresh()
                    showToast(getString(Res.string.withdraw_course_successful))
                    deleteDialogVisible = false
                    sCourse = null
                }
            }
        },
    )

    // 过滤
    CoursesFilterBottomSheet(
        filterBottomSheetVisible,
        onDismissRequest = { filterBottomSheetVisible = false },
        parameters = parameters,
        onParamsChange = {
            parameters = it
            filterBottomSheetVisible = false
        },
    )
}


/**
 * 课程。
 */
@Composable
private fun Course(
    course: SCourse,
    onSelect: () -> Unit,
    onWithdraw: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CardListItem(
        headlineContent = { Text(course.name) },
        modifier = modifier,
        supportingContent = {
            Column {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodySmall,
                ) {
                    Row {
                        // 课程编号
                        Text(course.courseId)
                        Spacer(modifier = Modifier.width(8.dp))
                        // 学分
                        Text("${course.credits}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        // 校区
                        Text(stringResource(Campus.entries[course.campusId - 1].nameRes))
                        Spacer(modifier = Modifier.width(8.dp))
                        // 选课人数
                        Text("${course.leftover} / ${course.total}")
                    }
                    // 教师
                    Text(course.teacher)
                    // 上课时间
                    course.time?.let { Text(it) }
                    // 上课地点
                    course.classroom?.let { Text(it) }
                    // 备注
                    course.note?.let { Text(it) }
                    // 冲突说明
                    course.conflict?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(it)
                    }
                }
            }
        },
        trailingContent = {
            TextButton(onClick = { if (course.isSelected) onWithdraw() else onSelect() }) {
                Text(
                    stringResource(
                        if (course.isSelected) Res.string.withdraw_course
                        else Res.string.select_course,
                    ),
                    color = if (course.isSelected) MaterialTheme.colorScheme.error
                    else LocalContentColor.current
                )
            }
        },
    )
}