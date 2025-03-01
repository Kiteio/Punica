package org.kiteio.punica.ui.page.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.ProgressModule
import org.kiteio.punica.client.academic.foundation.Term
import org.kiteio.punica.ui.component.ModalBottomSheet
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.recommended_term_index

@Composable
fun ModuleBottomSheet(visible: Boolean, onDismissRequest: () -> Unit, module: ProgressModule?) {
    val userId by AppVM.academicUserId.collectAsState(null)
    val terms by derivedStateOf { userId?.let { Term.list(it) } }

    ModalBottomSheet(visible, onDismissRequest) {
        module?.run {
            Column(modifier = Modifier.padding(16.dp)) {
                // 名称
                Text(moduleName, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(280.dp),
                ) {
                    items(progresses) {
                        Card(modifier = Modifier.padding(4.dp)) {
                            ListItem(
                                headlineContent = { Text(it.courseName) },
                                supportingContent = {
                                    Column {
                                        CompositionLocalProvider(
                                            LocalTextStyle provides MaterialTheme.typography.bodySmall,
                                        ) {
                                            // 课程编号
                                            Text(it.courseId)
                                            it.termIndex?.let { termIndex ->
                                                // 建议修读学期
                                                Row {
                                                    Text(stringResource(Res.string.recommended_term_index))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("${terms?.get(termIndex - 1) ?: termIndex}")
                                                }
                                            }
                                            // 备注
                                            it.note?.let { text ->
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(text)
                                            }
                                        }
                                    }
                                },
                                trailingContent = {
                                    Text(
                                        "${it.earnedCredits ?: 0} / ${it.credits}",
                                        color = if (
                                            (it.earnedCredits ?: 0.0) / it.credits < 1
                                        ) MaterialTheme.colorScheme.error
                                        else LocalContentColor.current,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}