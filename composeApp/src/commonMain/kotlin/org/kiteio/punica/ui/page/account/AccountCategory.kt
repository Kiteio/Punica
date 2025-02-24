package org.kiteio.punica.ui.page.account

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.CssGgIcons
import compose.icons.TablerIcons
import compose.icons.cssggicons.Dribbble
import compose.icons.tablericons.Wifi
import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.academic_system
import punica.composeapp.generated.resources.campus_network
import punica.composeapp.generated.resources.second_class

/**
 * 账号分类。
 */
enum class AccountCategory(val nameRes: StringResource, val icon: ImageVector) {
    /** 教务系统 */
    Academic(Res.string.academic_system, Icons.Outlined.School),

    /** 第二课堂 */
    SecondClass(Res.string.second_class, CssGgIcons.Dribbble),

    /** 校园网 */
    Network(Res.string.campus_network, TablerIcons.Wifi);
}