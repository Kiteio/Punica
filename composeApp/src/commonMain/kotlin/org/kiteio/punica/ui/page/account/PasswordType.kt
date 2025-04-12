package org.kiteio.punica.ui.page.account

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Security
import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Dribbble
import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.academic_system
import punica.composeapp.generated.resources.otp
import punica.composeapp.generated.resources.second_class

/**
 * 密码类型。
 */
enum class PasswordType(val nameRes: StringResource, val icon: ImageVector) {
    /** 教务系统密码 */
    Academic(Res.string.academic_system, Icons.Outlined.School),

    /** 第二课堂密码 */
    SecondClass(Res.string.second_class, CssGgIcons.Dribbble),

    /** OTP密码 */
    OTP(Res.string.otp, Icons.Outlined.Security);
}