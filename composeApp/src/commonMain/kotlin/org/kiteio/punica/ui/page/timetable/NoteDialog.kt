package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.close
import punica.composeapp.generated.resources.display_at_timetable_bottom
import punica.composeapp.generated.resources.note

/**
 * 备注对话框。
 *
 * @param note 备注
 * @param bottomNoteVisible 底部备注是否可见
 */
@Composable
fun NoteDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    note: String?,
    bottomNoteVisible: Boolean,
    onBottomNoteVisibleChange: () -> Unit,
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            // 关闭
            confirmButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(Res.string.close))
                }
            },
            title = { Text(stringResource(Res.string.note)) },
            text = {
                Column {
                    // 备注
                    note?.let {
                        Text(it)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    // 复选框：是否在底部展示
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            bottomNoteVisible,
                            onCheckedChange = { onBottomNoteVisibleChange() },
                        )
                        Text(stringResource(Res.string.display_at_timetable_bottom))
                    }
                }
            },
        )
    }
}