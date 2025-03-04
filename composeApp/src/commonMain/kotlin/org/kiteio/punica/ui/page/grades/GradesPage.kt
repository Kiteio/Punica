package org.kiteio.punica.ui.page.grades

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
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
import com.github.androidpasswordstore.sublimefuzzy.Fuzzy
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.ui.component.*
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.focusCleaner
import punica.composeapp.generated.resources.*

/**
 * 成绩页面路由。
 */
@Serializable
object GradesRoute : ModuleRoute {
    override val nameRes = Res.string.grade
    override val icon = Icons.AutoMirrored.Outlined.ReceiptLong
}


/**
 * 成绩页面。
 */
@Composable
fun GradesPage() = viewModel { GradesVM() }.Content()


@Composable
private fun GradesVM.Content() {
    val focusManager = LocalFocusManager.current

    val tabs = listOf(Res.string.course_grades, Res.string.qualification_grades)
    val state = rememberPagerState { tabs.size }
    var searchBarVisible by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var noteDialogVisible by remember { mutableStateOf(false) }

    // 监听账号，更新成绩
    LaunchedEffectCatching(AppVM.academicSystem) {
        updateGrades()
    }

    Scaffold(
        topBar = {
            NavBackAppBar(
                title = { Text(stringResource(GradesRoute.nameRes)) },
                shadowElevation = 0.dp,
                actions = {
                    // 搜索
                    SearchButton(
                        searchBarVisible,
                        isQueryBlank = query.isBlank(),
                        onClick = { searchBarVisible = !searchBarVisible },
                    )
                    // 备注
                    IconButton(onClick = { noteDialogVisible = true }) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = stringResource(Res.string.note),
                        )
                    }
                }
            )
        },
        modifier = Modifier.focusCleaner(focusManager),
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // 搜索
            AnimatedVisibility(searchBarVisible && state.currentPage == 0) {
                SearchTextField(
                    query,
                    onQueryChange = { query = it },
                    modifier = Modifier.padding(8.dp),
                )
            }
            LoadingNotNullOrEmpty(grades, isLoading = isGradesLoading) { grades ->
                // 成绩
                HorizontalTabPager(
                    state = state,
                    tabContent = { Text(stringResource(tabs[it])) },
                ) { page ->
                    Grades(
                        grades = when (page) {
                            0 -> grades.courses.run {
                                if (query.isNotBlank()) filter {
                                    Fuzzy.fuzzyMatchSimple(query, it.name) ||
                                            Fuzzy.fuzzyMatchSimple(query, "${it.term})")
                                }
                                else this
                            }

                            else -> grades.qualifications
                        }
                    )
                }
            }
        }
    }

    // 备注
    NoteDialog(
        noteDialogVisible,
        onDismissRequest = { noteDialogVisible = false },
        note = grades?.overview,
    )
}