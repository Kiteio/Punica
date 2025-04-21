package org.kiteio.punica.ui.page.totp

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.confirm
import punica.composeapp.generated.resources.confirm_to_delete
import punica.composeapp.generated.resources.dismiss

/**
 * 删除对话框。
 */
@Composable
fun DeleteDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(Res.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(Res.string.dismiss))
                }
            },
            text = {
                Text(stringResource(Res.string.confirm_to_delete))
            },
        )
    }
}