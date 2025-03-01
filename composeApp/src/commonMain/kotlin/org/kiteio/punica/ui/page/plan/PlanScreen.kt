package org.kiteio.punica.ui.page.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Rocket
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.Term
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.ui.component.HorizontalTabPager
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import punica.composeapp.generated.resources.*

/**
 * 执行计划页面路由。
 */
@Serializable
object PlanRoute : ModuleRoute {
    override val nameRes = Res.string.implementation_plan
    override val icon = TablerIcons.Rocket
}


/**
 * 执行计划页面。
 */
@Composable
fun PlanPage() = viewModel { PlanVM() }.Content()


@Composable
private fun PlanVM.Content() {
    LaunchedEffectCatching(AppVM.academicSystem) {
        updatePlans()
    }

    Scaffold(
        topBar = { NavBackAppBar(title = { Text(stringResource(PlanRoute.nameRes)) }) },
    ) { innerPadding ->
        LoadingNotNullOrEmpty(
            plans?.plans,
            isLoading = isLoading,
            modifier = Modifier.padding(innerPadding),
        ) { plans ->
            val tabs = plans.map { it.term }.toSet().sortedBy { it.startYear + it.ordinal }
            val state = rememberPagerState { tabs.size }

            LaunchedEffectCatching(Unit) {
                state.requestScrollToPage(tabs.indexOf(Term.current))
            }

            HorizontalTabPager(
                state,
                tabContent = { Text("${tabs[it]}") },
            ) { page ->
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(200.dp),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(plans.filter { it.term == tabs[page] }) {
                        ElevatedCard(onClick = {}, modifier = Modifier.padding(4.dp)) {
                            ListItem(
                                headlineContent = { Text(it.courseName) },
                                supportingContent = {
                                    Column {
                                        Row {
                                            // 课程编号
                                            Text("${stringResource(Res.string.course_id)} ${it.courseId}")
                                            Spacer(modifier = Modifier.width(16.dp))
                                            // 总学时
                                            Text("${stringResource(Res.string.class_hours)} ${it.hours}")
                                        }
                                        // 课程属性
                                        Text("${stringResource(Res.string.course_category)} ${it.category}")
                                        Spacer(modifier = Modifier.height(4.dp))
                                        // 开课单位
                                        Text(
                                            it.courseProvider,
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                },
                                trailingContent = { Text(it.assessmentMethod) },
                            )
                        }
                    }
                }
            }
        }
    }
}