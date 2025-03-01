package org.kiteio.punica.ui.page.secondclass

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.kiteio.punica.client.secondclass.api.GradeLog
import org.kiteio.punica.client.secondclass.api.getGradeLogs
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.wrapper.launchCatching

/**
 * 成绩获取记录。
 */
@Composable
fun SecondClassVM.GradeLogs() {
    var isLoading by remember { mutableStateOf(true) }
    val logsOrNull by produceState<List<GradeLog>?>(null, secondClass) {
        launchCatching {
            try {
                value = secondClass?.getGradeLogs()
            } finally {
                isLoading = false
            }
        }
    }

    LoadingNotNullOrEmpty(
        logsOrNull,
        isLoading = isLoading,
        modifier = Modifier.fillMaxSize(),
    ) { logs ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(200.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(logs) {
                GradeLog(it, modifier = Modifier.padding(4.dp))
            }
        }
    }
}


/**
 * 成绩获取记录。
 */
@Composable
private fun GradeLog(gradeLog: GradeLog, modifier: Modifier = Modifier) {
    ElevatedCard(onClick = {}, modifier = modifier) {
        ListItem(
            headlineContent = { Text(gradeLog.activityName) },
            supportingContent = {
                Column {
                    Text(gradeLog.term)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        buildAnnotatedString {
                            // 分类和分数
                            withStyle(
                                LocalTextStyle.current.copy(
                                    fontWeight = FontWeight.Bold
                                ).toSpanStyle(),
                            ) {
                                append(gradeLog.category)
                                append("  ")
                                append("${gradeLog.score}")
                                append("  ")
                            }
                            // 获取时间
                            append(gradeLog.time)
                        },
                    )
                }
            }
        )
    }
}