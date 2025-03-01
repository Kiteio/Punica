package org.kiteio.punica.ui.page.timetables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.androidpasswordstore.sublimefuzzy.Fuzzy
import compose.icons.TablerIcons
import compose.icons.tablericons.Book
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.CCourse
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.ui.widget.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.widget.NavBackAppBar
import org.kiteio.punica.ui.widget.SearchButton
import org.kiteio.punica.ui.widget.SearchTextField
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.focusCleaner
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.course_timetable

/**
 * 课程课表页面路由。
 */
@Serializable
object CourseTimetableRoute : ModuleRoute {
    override val nameRes = Res.string.course_timetable
    override val icon = TablerIcons.Book
}


/**
 * 课程课表页面。
 */
@Composable
fun CourseTimetablePage() = viewModel { CourseTimetableVM() }.Content()


@Composable
private fun CourseTimetableVM.Content() {
    val focusManager = LocalFocusManager.current

    LaunchedEffectCatching(AppVM.academicSystem) {
        updateTimetable()
    }

    var bottomSheetVisible by remember { mutableStateOf(false) }
    var courses by remember { mutableStateOf<List<CCourse>?>(null) }

    var searchBarVisible by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            NavBackAppBar(
                title = { Text(stringResource(CourseTimetableRoute.nameRes)) },
                actions = {
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
            // 课程
            LoadingNotNullOrEmpty(
                timetable,
                isLoading = isLoading,
            ) { timetable ->
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(280.dp),
                    contentPadding = PaddingValues(4.dp),
                ) {
                    items(
                        timetable.courses.run {
                            if (query.isNotBlank()) filter {
                                // 过滤课程名称
                                Fuzzy.fuzzyMatchSimple(query, it.first().name) || it.any { course ->
                                    // 过滤教师
                                    course.teacher != null && Fuzzy.fuzzyMatchSimple(query, course.teacher) ||
                                            // 过滤教室
                                            course.classroom != null && Fuzzy.fuzzyMatchSimple(query, course.classroom)
                                }
                            } else this
                        }.sortedBy { it.first().name }
                    ) {
                        ElevatedCard(
                            onClick = {
                                courses = it
                                bottomSheetVisible = true
                            },
                            modifier = Modifier.padding(4.dp),
                        ) {
                            ListItem(
                                headlineContent = { Text(it.first().name) },
                                supportingContent = {
                                    // 教师
                                    Text(
                                        it.filter { it.teacher != null }
                                            .map { course -> course.teacher }
                                            .toSet()
                                            .joinToString(),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // 课程列表模态对话框
    CoursesBottomSheet(
        bottomSheetVisible,
        onDismissRequest = {
            bottomSheetVisible = false
            courses = null
        },
        courses = courses,
    )
}