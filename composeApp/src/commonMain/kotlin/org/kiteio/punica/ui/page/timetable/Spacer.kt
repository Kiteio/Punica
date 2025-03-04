package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 课表餐点空白。
 *
 * @param rowIndex 行下标
 */
@Composable
fun TimetableMealSpacer(rowIndex: Int) {
    if (rowIndex == 2 || rowIndex == 4) Spacer(
        modifier = Modifier.fillMaxWidth().height(4.dp).background(
            MaterialTheme.colorScheme.surfaceContainerLow,
        )
    )
}