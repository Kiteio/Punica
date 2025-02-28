package org.kiteio.punica.ui.page.course

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Slack
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.course.foundation.CourseCategory
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.ui.page.timetable.TimetableRoute
import org.kiteio.punica.ui.widget.*
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.focusCleaner
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.course_system
import punica.composeapp.generated.resources.log
import punica.composeapp.generated.resources.note

/**
 * 选课系统页面路由。
 */
@Serializable
object CourseSystemRoute : ModuleRoute {
    override val nameRes = Res.string.course_system
    override val icon = CssGgIcons.Slack
}


/**
 * 选课系统页面。
 */
@Composable
fun CourseSystemPage() = viewModel { CourseSystemVM() }.Content()


@Composable
private fun CourseSystemVM.Content() {
    val focusManager = LocalFocusManager.current

    var searchBarVisible by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    var logsBottomSheetVisible by remember { mutableStateOf(false) }
    var overviewBottomSheetVisible by remember { mutableStateOf(false) }
    var myCoursesBottomSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffectCatching(AppVM.academicSystem) {
        updateCourseSystem()
    }

    Scaffold(
        topBar = {
            NavBackAppBar(
                title = { Text(stringResource(CourseSystemRoute.nameRes)) },
                actions = {
                    // 日志
                    IconButton(
                        onClick = { logsBottomSheetVisible = true },
                        enabled = courseSystem != null,
                    ) {
                        Icon(
                            Icons.Outlined.History,
                            contentDescription = stringResource(Res.string.log),
                        )
                    }
                    // 信息
                    IconButton(
                        onClick = { overviewBottomSheetVisible = true },
                        enabled = courseSystem != null,
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = stringResource(Res.string.note),
                        )
                    }
                    // 已选课程
                    IconButton(
                        onClick = { myCoursesBottomSheetVisible = true },
                        enabled = courseSystem != null,
                    ) {
                        Icon(
                            TimetableRoute.icon,
                            contentDescription = stringResource(TimetableRoute.nameRes),
                        )
                    }
                    // 搜索
                    SearchButton(
                        searchBarVisible,
                        isQueryBlank = query.isBlank(),
                        onClick = { searchBarVisible = !searchBarVisible },
                    )
                }
            )
        },
        modifier = Modifier.focusCleaner(focusManager),
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // 搜索
            AnimatedVisibility(searchBarVisible) {
                SearchTextField(
                    query,
                    onQueryChange = { query = it },
                    modifier = Modifier.padding(8.dp),
                )
            }
            LoadingNotNullOrEmpty(
                courseSystem,
                isLoading = isLoading,
            ) {
                val state = rememberPagerState { CourseCategory.entries.size }

                HorizontalTabPager(
                    state,
                    tabContent = {
                        Text(stringResource(CourseCategory.entries[it].nameRes))
                    },
                    tabScrollable = true,
                ) { page ->
                    Courses(
                        it,
                        category = CourseCategory.entries[page],
                        query = query,
                    )
                }
            }
        }
    }

    LogsBottomSheet(
        logsBottomSheetVisible,
        onDismissRequest = { logsBottomSheetVisible = false },
        courseSystem = courseSystem,
    )

    OverviewBottomSheet(
        overviewBottomSheetVisible,
        onDismissRequest = { overviewBottomSheetVisible = false },
        courseSystem = courseSystem,
    )

    MyCoursesBottomSheet(
        myCoursesBottomSheetVisible,
        onDismissRequest = { myCoursesBottomSheetVisible = false },
        courseSystem = courseSystem,
    )
}