package org.kiteio.punica.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.clear
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
                    onCheckedChange = {},
                ) {
                    Icon(
                        if (query.isNotBlank()) Icons.Outlined.Search
                        else Icons.Outlined.SearchOff,
                        contentDescription = stringResource(Res.string.search),
                    )
                }
            },
            trailingIcon = if (query.isNotBlank()) {
                {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = stringResource(Res.string.clear),
                        )
                    }
                }
            } else null,
            colors = SearchBarDefaults.inputFieldColors(
                unfocusedContainerColor = color,
                focusedContainerColor = color,
            ),
        )
    }
}