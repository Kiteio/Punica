package org.kiteio.punica.ui.page.secondclass

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.kiteio.punica.ui.component.CardListItem
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.wrapper.LaunchedEffectCatching

/**
 * 成绩。
 */
@Composable
fun SecondClassVM.Grades() {
    val icons = listOf(
        Icons.Rounded.Flag,
        Icons.Rounded.TipsAndUpdates,
        Icons.Rounded.VolunteerActivism,
        Icons.Rounded.Diversity2,
        Icons.Rounded.ColorLens,
        Icons.Rounded.Spa,
        Icons.Rounded.AutoGraph
    )

    // 监听账号，更新成绩
    LaunchedEffectCatching(secondClass) {
        updateGrades()
    }

    LoadingNotNullOrEmpty(
        grades,
        isLoading = isGradesLoading,
        modifier = Modifier.fillMaxSize(),
    ) { grades ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(232.dp),
            contentPadding = PaddingValues(8.dp),
        ) {
            itemsIndexed(grades.grades) { index, grade ->
                CardListItem(
                    // 名称
                    headlineContent = { Text(grade.name) },
                    onClick = {},
                    modifier = Modifier.padding(8.dp),
                    // 图标
                    leadingContent = {
                        Icon(icons[index], contentDescription = grade.name)
                    },
                    // 成绩比分
                    trailingContent = {
                        Text(
                            "${grade.score} / ${grade.requiredScore}",
                            color = if (
                                grade.score < grade.requiredScore
                            ) MaterialTheme.colorScheme.error
                            else LocalContentColor.current,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                )
            }
        }
    }
}