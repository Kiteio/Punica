package org.kiteio.punica.ui.theme

import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.theme_mode_auto
import punica.composeapp.generated.resources.theme_mode_dark
import punica.composeapp.generated.resources.theme_mode_light

/**
 * 主题模式。
 *
 * @param nameRes 名称字符串资源
 */
enum class ThemeMode(val nameRes: StringResource) {
    /** 跟随系统 */
    Default(Res.string.theme_mode_auto),

    /** 亮色 */
    Light(Res.string.theme_mode_light),

    /** 暗色 */
    Dark(Res.string.theme_mode_dark);
}