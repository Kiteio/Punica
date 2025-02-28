package org.kiteio.punica.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val focusManager = LocalFocusManager.current
        val color = if (query.isNotBlank()) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHigh

        SearchBarDefaults.InputField(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { focusManager.clearFocus() },
            expanded = false,
            onExpandedChange = {},
            modifier = modifier,
            leadingIcon = {
                IconToggleButton(
                    checked = query.isNotBlank(),
                    onCheckedChange = {
                        onQueryChange("")
                    },
                ) {
                    Icon(
                        if (query.isNotBlank()) Icons.Outlined.SearchOff
                        else Icons.Outlined.Search,
                        contentDescription = stringResource(Res.string.search),
                    )
                }
            },
            colors = SearchBarDefaults.inputFieldColors(
                unfocusedContainerColor = color,
                focusedContainerColor = color,
            ),
        )
    }
}