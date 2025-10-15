package org.kiteio.punica.mirror.modal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face4
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.outlined.Face4
import androidx.compose.material.icons.outlined.FaceRetouchingNatural
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.boy
import punica.composeapp.generated.resources.girl

/**
 * 性别。
 *
 * @property strRes 性别名称字符串资源
 * @property avatar 头像
 * @property selectedAvatar 选中头像
 */
enum class Gender(
    val strRes: StringResource,
    val avatar: ImageVector,
    val selectedAvatar: ImageVector,
) {
    /** 女生 */
    Girl(
        strRes = Res.string.girl,
        avatar = Icons.Outlined.Face4,
        selectedAvatar = Icons.Filled.Face4,
    ),

    /** 男生 */
    Boy(
        strRes = Res.string.boy,
        avatar = Icons.Outlined.FaceRetouchingNatural,
        selectedAvatar = Icons.Filled.FaceRetouchingNatural
    );

    companion object {
        val Default = Girl
    }
}