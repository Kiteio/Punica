package org.kiteio.punica.ui.page.grades

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.kiteio.punica.client.academic.api.CourseGrade
import org.kiteio.punica.client.academic.api.Grade
import org.kiteio.punica.client.academic.api.QualificationGrade
import org.kiteio.punica.ui.component.CardListItem

/**
 * 成绩。
 */
@Composable
fun Grades(grades: List<Grade>) {
    var gradeBottomSheetVisible by remember { mutableStateOf(false) }
    var courseGrade by remember { mutableStateOf<CourseGrade?>(null) }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(232.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(grades) { grade ->
            CardListItem(
                // 名称
                headlineContent = { Text(grade.name) },
                onClick = {
                    if (grade is CourseGrade) {
                        courseGrade = grade
                        gradeBottomSheetVisible = true
                    }
                },
                modifier = Modifier.padding(8.dp),
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

    GradeBottomSheet(
        gradeBottomSheetVisible,
        onDismissRequest = {
            gradeBottomSheetVisible = false
            courseGrade = null
        },
        grade = courseGrade,
    )
}