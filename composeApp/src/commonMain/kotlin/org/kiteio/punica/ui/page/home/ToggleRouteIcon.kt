package org.kiteio.punica.ui.page.home

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource

/**
 * 可变换路由图标。
 *
 * @param selected 是否选中
 * @param topLevelRoute 顶层路由
 */
@Composable
fun ToggleRouteIcon(selected: Boolean, topLevelRoute: TopLevelRoute) {
    Icon(
        imageVector = if (selected) topLevelRoute.toggledIcon else topLevelRoute.icon,
        contentDescription = stringResource(topLevelRoute.nameRes),
        tint = if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current,
    )
}