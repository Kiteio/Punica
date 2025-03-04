package org.kiteio.punica.ui.page.course

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.kiteio.punica.client.course.CourseSystem
import org.kiteio.punica.client.course.api.Overview
import org.kiteio.punica.client.course.api.getOverview
import org.kiteio.punica.ui.component.CardListItem
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.ModalBottomSheet
import org.kiteio.punica.wrapper.launchCatching

/**
 * 选课系统概览。
 */
@Composable
fun OverviewBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    courseSystem: CourseSystem?,
) {
    ModalBottomSheet(visible, onDismissRequest) {
        var isLoading by remember { mutableStateOf(true) }
        val overviewOrNull by produceState<Overview?>(null) {
            launchCatching {
                try {
                    value = courseSystem?.getOverview()
                } finally {
                    isLoading = false
                }
            }
        }

        LoadingNotNullOrEmpty(overviewOrNull, isLoading = isLoading) { overview ->
            Column {
                // 学分进度
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(232.dp),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(overview.progresses) {
                        CardListItem(
                            headlineContent = { Text(it.name) },
                            modifier = Modifier.padding(8.dp),
                            trailingContent = {
                                Text(
                                    "${it.have} / ${it.limit}",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                        )
                    }
                }
                // 备注
                Text(
                    overview.note,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}