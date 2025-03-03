package org.kiteio.punica.ui.page.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import compose.icons.TablerIcons
import compose.icons.tablericons.Id
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.Teacher
import org.kiteio.punica.ui.component.Loading
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.ui.component.SearchButton
import org.kiteio.punica.ui.component.SearchTextField
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.wrapper.Pager
import org.kiteio.punica.wrapper.focusCleaner
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.teacher_profile

/**
 * 教师页面路由。
 */
@Serializable
object TeacherProfileRoute : ModuleRoute {
    override val nameRes = Res.string.teacher_profile
    override val icon = TablerIcons.Id
}

/**
 * 教师页面。
 */
@Composable
fun TeacherProfilePage() = Content()


@Composable
private fun Content() {
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }
    val teachers = remember(AppVM.academicSystem, query) {
        Pager(pageSize = 1) { TeachersPagingSource(query) }.flow
    }.collectAsLazyPagingItems()
    var searchBarVisible by remember { mutableStateOf(false) }

    var bottomSheetVisible by remember { mutableStateOf(false) }
    var teacher by remember { mutableStateOf<Teacher?>(null) }

    Scaffold(
        topBar = {
            NavBackAppBar(
                title = { Text(stringResource(TeacherProfileRoute.nameRes)) },
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

            Loading(teachers) {
                // 教师
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(200.dp),
                    contentPadding = PaddingValues(4.dp),
                ) {
                    items(teachers.itemCount) { index ->
                        teachers[index]?.let {
                            Teacher(
                                it,
                                onClick = {
                                    teacher = it
                                    bottomSheetVisible = true
                                },
                                modifier = Modifier.padding(4.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    // 教师模态对话框
    TeacherBottomSheet(
        bottomSheetVisible,
        onDismissRequest = {
            bottomSheetVisible = false
            teacher = null
        },
        teacher = teacher,
    )
}


/**
 * 教师。
 */
@Composable
private fun Teacher(teacher: Teacher, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(onClick = onClick, modifier = modifier) {
        ListItem(
            // 姓名
            headlineContent = { Text(teacher.name) },
            // 院系
            supportingContent = teacher.faculty?.let { { Text(it) } },
            // 工号
            trailingContent = {
                Text(
                    teacher.id,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}