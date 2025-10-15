package org.kiteio.punica.mirror.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.StringResource

/**
 * 底部导航 [NavKey]。
 *
 * @property strRes 名称字符串资源
 * @property icon 图标
 * @property selectedIcon 选中图标
 */
interface BottomNavKey : NavKey {
    val strRes: StringResource
    val icon: ImageVector
    val selectedIcon: ImageVector
}