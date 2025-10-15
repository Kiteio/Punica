package org.kiteio.punica.mirror.modal.education

import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.*

/**
 * 校区。
 *
 * @property strRes 校区名称字符串资源
 * @property areaStrRes 地区名称字符串资源
 */
enum class Campus(
    val strRes: StringResource,
    val areaStrRes: StringResource,
) {
    /** 广州校区 */
    Canton(
        strRes = Res.string.campus_canton,
        areaStrRes = Res.string.canton,
    ) {
        override val schedule get() = cantonSchedule
    },

    /** 佛山校区 */
    Foshan(
        strRes = Res.string.campus_foshan,
        areaStrRes = Res.string.foshan,
    ) {
        override val schedule get() = foshanSchedule
    };

    val id get() = ordinal + 1

    /** 时间表 */
    abstract val schedule: List<ClosedRange<String>>

    companion object {
        val Default = Canton

        /**
         * 通过 [id] 获取校区。
         */
        fun getById(id: Int): Campus {
            require(id in 1..Campus.entries.size)

            return when (id) {
                Canton.id -> Canton
                else -> Foshan
            }
        }

        /**
         * 通过名字获取校区。
         */
        fun getByName(name: String) = when (name) {
            "广州校区" -> Canton
            "佛山校区" -> Foshan
            else -> error("Unknown campus: $name.")
        }
    }
}

/** 广州校区时间表 */
private val cantonSchedule by lazy {
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

/** 佛山校区时间表 */
private val foshanSchedule by lazy {
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