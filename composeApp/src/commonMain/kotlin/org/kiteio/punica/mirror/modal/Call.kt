package org.kiteio.punica.mirror.modal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocalPolice
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.*

/**
 * 电话。
 *
 * @property strRes 名称字符串资源
 * @property icon 图标
 * @property phoneNumber 电话号码
 * @property workingHours 工作时间
 */
data class Call(
    val strRes: StringResource,
    val icon: ImageVector,
    val phoneNumber: String,
    val workingHours: ClosedRange<LocalTime>? = null,
) {
    companion object {
        /** 广州 */
        val canton by lazy {
            listOf(
                // 门诊部急救
                Call(
                    strRes = Res.string.first_aid,
                    icon = Icons.Outlined.LocalHospital,
                    phoneNumber = "13112234297",
                ),
                // 校园报警
                Call(
                    strRes = Res.string.campus_alarm,
                    icon = Icons.Outlined.LocalPolice,
                    phoneNumber = "020-84096060",
                ),
                // 校园警务室
                Call(
                    strRes = Res.string.campus_police_office,
                    icon = Icons.Outlined.LocalPolice,
                    phoneNumber = "020-84096110",
                    workingHours = LocalTime(8, 30)..LocalTime(17, 30),
                ),
                // 官洲派出所
                Call(
                    strRes = Res.string.guanzhou_police_station,
                    icon = Icons.Outlined.LocalPolice,
                    phoneNumber = "020-84092782",
                ),
            )
        }

        /** 佛山 */
        val foshan by lazy {
            listOf(
                // 门诊部急救
                Call(
                    strRes = Res.string.first_aid,
                    icon = Icons.Outlined.LocalHospital,
                    phoneNumber = "18566890063",
                ),
                // 校园报警
                Call(
                    strRes = Res.string.campus_alarm,
                    icon = Icons.Outlined.LocalPolice,
                    phoneNumber = "0757-87828110",
                ),
            )
        }
    }
}