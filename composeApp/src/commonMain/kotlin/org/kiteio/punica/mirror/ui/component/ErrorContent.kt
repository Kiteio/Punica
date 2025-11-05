package org.kiteio.punica.mirror.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.error

/**
 * 出错内容。
 */
@Composable
fun ErrorContent(
    throwable: Throwable,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 图标
        Icon(
            Icons.Outlined.ErrorOutline,
            contentDescription = stringResource(Res.string.error),
            modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        // 提示
        Text(
            stringResource(Res.string.error),
        )
        Spacer(modifier = Modifier.height(8.dp))
        // 异常内容
        Text(
            throwable.message ?: "$throwable",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}