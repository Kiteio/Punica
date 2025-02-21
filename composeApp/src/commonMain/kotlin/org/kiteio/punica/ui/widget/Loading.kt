package org.kiteio.punica.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.nothing_provided

/**
 * 提供非空内容。
 *
 * @param isLoading 是否正在加载中
 */
@Composable
fun <T> ProvideNotNull(any: T?, isLoading: Boolean, modifier: Modifier = Modifier, content: @Composable T.() -> Unit) {
    Box(modifier = modifier) {
        when {
            isLoading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            any != null -> content(any)
            else -> Empty()
        }
    }
}


/**
 * 空内容。
 */
@Composable
private fun Empty() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val text = stringResource(Res.string.nothing_provided)

        Icon(
            Icons.Outlined.ErrorOutline,
            contentDescription = text,
            modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text)
    }
}