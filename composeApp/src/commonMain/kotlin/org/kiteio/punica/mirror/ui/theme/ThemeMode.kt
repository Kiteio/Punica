package org.kiteio.punica.mirror.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.theme_mode_auto
import punica.composeapp.generated.resources.theme_mode_dark
import punica.composeapp.generated.resources.theme_mode_light

/**
 * 主题模式。
 *
 * @property strRes 名称字符串资源
 * @property icon 图标
 */
enum class ThemeMode(
    val strRes: StringResource,
    val icon: ImageVector,
) {
    /** 自动 */
    Auto(
        strRes = Res.string.theme_mode_auto,
        icon = Icons.Outlined.AutoMode
    ),

    /** 亮色 */
    Light(
        strRes = Res.string.theme_mode_light,
        icon = Icons.Outlined.LightMode
    ),

    /** 暗色 */
    Dark(
        strRes = Res.string.theme_mode_dark,
        icon = Icons.Outlined.DarkMode
    );

    companion object {
        val Default = Auto
    }
}