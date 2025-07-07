package org.kiteio.punica.mirror.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

/**
 * 导航描述。
 *
 * @property route 路由
 * @property name 名称
 * @property icon 图标
 */
data class NavDescription(
    val route: Any,
    val name: StringResource,
    val icon: ImageVector,
)