package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.error
import punica.composeapp.generated.resources.nothing_provided

/**
 * 提供非空内容。
 *
 * @param isLoading 是否正在加载中
 */
@Composable
fun <T> LoadingNotNullOrEmpty(
    any: T?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    Box(modifier = modifier) {
        when {
            isLoading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            any != null && (any !is List<*> || any.isNotEmpty()) -> content(any)
            else -> Empty()
        }
    }
}


/**
 * 加载中。
 */
@Composable
fun Loading(
    items: LazyPagingItems<*>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        when (items.loadState.refresh) {
            LoadState.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            is LoadState.Error -> Empty(
                icon = Icons.Outlined.ErrorOutline,
                text = stringResource(Res.string.error)
            )

            else -> if (items.itemCount == 0) Empty() else content()
        }
    }
}


/**
 * 空内容。
 */
@Composable
fun Empty(
    icon: ImageVector = Icons.Outlined.Inbox,
    text: String = stringResource(Res.string.nothing_provided),
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            icon,
            contentDescription = text,
            modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text)
    }
}