package org.kiteio.punica.ui.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * 提供 BodyMedium 字体。
 */
@Composable
fun ProvideBodyMedium(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.bodyMedium,
        content = content,
    )
}