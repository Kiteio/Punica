package org.kiteio.punica.ui.page.grades

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.kiteio.punica.client.academic.api.CourseGrade
import org.kiteio.punica.client.academic.api.Grade
import org.kiteio.punica.client.academic.api.QualificationGrade

/**
 * 成绩。
 */
@Composable
fun Grades(grades: List<Grade>) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(200.dp),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(grades) { grade ->
            ElevatedCard(
                onClick = {},
                modifier = Modifier.padding(4.dp),
            ) {
                ListItem(
                    // 名称
                    headlineContent = { Text(grade.name) },
                    // 时间
                    supportingContent = {
                        when (grade) {
                            is CourseGrade -> Text("${grade.term}")
                            is QualificationGrade -> Text("${grade.date}")
                        }
                    },
                    // 成绩
                    trailingContent = {
                        Text(
                            grade.score,
                            // 不及格课程成绩显示为红色
                            color = if (
                                grade.score.all { it.isDigit() } && grade.score.toDouble() < 60
                            ) MaterialTheme.colorScheme.error
                            else LocalContentColor.current,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                )
            }
        }
    }
}