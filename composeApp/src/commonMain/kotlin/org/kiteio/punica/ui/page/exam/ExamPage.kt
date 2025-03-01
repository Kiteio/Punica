package org.kiteio.punica.ui.page.exam

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Time
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.format
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.exam

/**
 * 考试安排页面路由。
 */
@Serializable
object ExamRoute : ModuleRoute {
    override val nameRes = Res.string.exam
    override val icon = CssGgIcons.Time
}


/**
 * 考试安排页面。
 */
@Composable
fun ExamPage() = viewModel { ExamVM() }.Content()


@Composable
private fun ExamVM.Content() {
    LaunchedEffectCatching(AppVM.academicSystem) {
        updateExams()
    }

    Scaffold(
        topBar = { NavBackAppBar(title = { Text(stringResource(ExamRoute.nameRes)) }) },
    ) { innerPadding ->
        LoadingNotNullOrEmpty(
            exams?.exams,
            isLoading = isLoading,
            modifier = Modifier.padding(innerPadding),
        ) { exams ->
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(200.dp),
                contentPadding = PaddingValues(4.dp),
            ) {
                items(exams) {
                    ElevatedCard(modifier = Modifier.padding(4.dp)) {
                        ListItem(
                            headlineContent = { Text(it.courseName) },
                            supportingContent = {
                                Column {
                                    // 考试时间
                                    Text("${it.duration.start.format()} - ${it.duration.endInclusive.format()}")
                                    // 地点
                                    Text(it.classroom)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    // 课程编号
                                    Text(
                                        it.courseId,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            },
                            trailingContent = { Text(stringResource(it.campus.nameRes)) },
                        )
                    }
                }
            }
        }
    }
}