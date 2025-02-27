package org.kiteio.punica.ui.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.search

/**
 * 搜索按钮。
 */
@Composable
fun SearchButton(checked: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            if (checked) Icons.Outlined.SearchOff
            else Icons.Outlined.Search,
            contentDescription = stringResource(Res.string.search),
        )
    }
}