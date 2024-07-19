package org.kiteio.punica.ui.component

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.font.FontWeight


@Composable
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    selected: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    DropdownMenuItem(
        text = {
            CompositionLocalProvider(
                value = LocalTextStyle provides LocalTextStyle.current.run {
                    if (selected) copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ) else this
                }
            ) {
                text()
            }
        },
        onClick = onClick,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}