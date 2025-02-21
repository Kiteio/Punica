package org.kiteio.punica.ui.page.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * 可变换文字。
 *
 * @param selected 是否选中
 */
@Composable
fun ToggleText(selected: Boolean, text: String) {
    Text(
        text,
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Unspecified,
    )
}