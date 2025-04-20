package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.ICourse
import org.kiteio.punica.ui.component.Checkbox
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.NoteDialog
import org.kiteio.punica.ui.page.home.TopLevelRoute
import org.kiteio.punica.ui.rememberRunBlocking
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.display_at_timetable_bottom
import punica.composeapp.generated.resources.timetable

/**
 * 课表页面路由。
 */
@Serializable
object TimetableRoute : TopLevelRoute {
    override val nameRes = Res.string.timetable
    override val icon = Icons.Outlined.DateRange
    override val toggledIcon = Icons.Filled.DateRange
}

/**
 * 课表页面。
 */
@Composable
fun TimetablePage() = viewModel { TimetableVM() }.Content()


@Composable
private fun TimetableVM.Content() {
    val scope = rememberCoroutineScope()
    val week = rememberRunBlocking { AppVM.weekFlow.first() }
    val state = rememberPagerState(initialPage = week) { AppVM.TIMETABLE_MAX_PAGE }

    // 备注对话框可见性
    var noteDialogVisible by remember { mutableStateOf(false) }

    // 课程对话框可见性
    var coursesDialogVisible by remember { mutableStateOf(false) }
    var visibleCourses by remember { mutableStateOf<List<ICourse>?>(null) }

    // 监听账号和学期变化，更新课表
    LaunchedEffectCatching(AppVM.academicSystem, term) {
        updateTimetable()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                week = week,
                currentPage = state.currentPage,
                onPageChange = { scope.launchCatching { state.requestScrollToPage(it) } },
                onNoteDialogDisplayRequest = { noteDialogVisible = true },
            )
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { innerPadding ->
        LoadingNotNullOrEmpty(
            timetable,
            isLoading = isLoading,
            modifier = Modifier.padding(innerPadding),
        ) { timetable ->
            Timetable(
                state = state,
                week = week,
                courses = timetable.cells,
                onItemClick = {
                    visibleCourses = timetable.cells[it]
                    coursesDialogVisible = true
                },
                note = timetable.note,
                noteVisible = bottomNoteVisible,
            )
        }
    }

    // 备注对话框
    NoteDialog(
        visible = noteDialogVisible,
        onDismissRequest = { noteDialogVisible = false },
        note = timetable?.note,
    ) {
        // 复选框：是否在底部展示
        Checkbox(
            bottomNoteVisible,
            onCheckedChange = { switchBottomNoteVisible() },
            label = { Text(stringResource(Res.string.display_at_timetable_bottom)) },
        )
    }

    CoursesDialog(
        coursesDialogVisible,
        onDismissRequest = {
            coursesDialogVisible = false
            visibleCourses = null
        },
        courses = visibleCourses,
    )
}