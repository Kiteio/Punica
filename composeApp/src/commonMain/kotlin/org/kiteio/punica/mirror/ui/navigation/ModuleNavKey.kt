package org.kiteio.punica.mirror.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.StringResource

/**
 * 模块 [NavKey]。
 *
 * @property strRes 名称字符串资源
 * @property icon 图标
 */
interface ModuleNavKey: NavKey {
    val strRes: StringResource
    val icon: ImageVector
}