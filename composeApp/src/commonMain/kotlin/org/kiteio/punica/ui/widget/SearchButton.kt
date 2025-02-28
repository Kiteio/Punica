package org.kiteio.punica.ui.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.search

/**
 * 搜索按钮。
 */
@Composable
fun SearchButton(checked: Boolean, isQueryBlank: Boolean, onClick: () -> Unit) {
    IconToggleButton(!isQueryBlank, onCheckedChange = { onClick() }) {
        Icon(
            if (checked) Icons.Outlined.Search
            else Icons.Filled.Search,
            contentDescription = stringResource(Res.string.search),
        )
    }
}