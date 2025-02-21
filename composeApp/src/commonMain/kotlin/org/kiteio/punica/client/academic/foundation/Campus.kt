package org.kiteio.punica.client.academic.foundation

import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.campus_canton
import punica.composeapp.generated.resources.campus_foshan

/**
 * 校区。
 *
 * @property nameRes 名称字符串资源
 */
enum class Campus(val nameRes: StringResource) {
    /** 广州校区 */
    CANTON(Res.string.campus_canton) {
        override val schedule by lazy {
            listOf(
                "08:00".."08:45",
                "08:55".."09:40",
                "10:00".."10:45",
                "10:55".."11:40",

                "14:10".."14:55",
                "15:05".."15:50",
                "16:10".."16:55",
                "17:05".."17:50",

                "18:40".."19:25",
                "19:35".."20:20",
                "20:30".."21:15",
                "21:25".."22:10",
            )
        }
    },

    /** 佛山校区 */
    FO_SHAN(Res.string.campus_foshan) {
        override val schedule by lazy {
            listOf(
                "08:30".."09:15",
                "09:15".."10:00",
                "10:20".."11:05",
                "11:05".."11:50",

                "14:00".."14:45",
                "14:45".."15:30",
                "15:50".."16:35",
                "16:35".."17:20",

                "18:30".."19:15",
                "19:15".."20:00",
                "20:20".."21:05",
                "21:05".."21:50",
            )
        }
    };

    /** 上课时间表 */
    abstract val schedule: List<ClosedRange<String>>
}


/**
 * 校区唯一标识。
 */
val Campus.id get() = ordinal + 1