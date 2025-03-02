package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.font.FontWeight
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink

/**
 * [SettingsGroup]。
 */
@Composable
fun SettingsGroup(
    title: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    SettingsGroup(
        title = {
            // 调小字体并加粗
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                content = title,
            )
        },
        content = content,
    )
}


/**
 * [SettingsMenuLink]。
 */
@Composable
fun SettingsMenuLink(
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)?,
    icon: @Composable () -> Unit,
    action: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    SettingsMenuLink(
        title = {
            // 设置字体颜色 onSurface
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                content = title,
            )
        },
        subtitle = subtitle?.run {
            {
                // 设置字体透明度 0.6f
                CompositionLocalProvider(
                    LocalContentColor provides LocalContentColor.current.copy(0.6f),
                    content = subtitle,
                )
            }
        },
        icon = icon,
        action = action,
        onClick = onClick,
    )
}