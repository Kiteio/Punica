package org.kiteio.punica.mirror.modal.education

import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.campus_canton
import punica.composeapp.generated.resources.campus_foshan

/**
 * 校区。
 *
 * @property nameRes 名称资源
 * @property schedule 时间表
 */
sealed class Campus(val id: Int) {
    abstract val nameRes: StringResource
    abstract val schedule: List<ClosedRange<String>>

    /** 广州校区 */
    data object Canton : Campus(1) {
        override val nameRes = Res.string.campus_canton
        override val schedule get() = cantonSchedule
    }

    /** 佛山校区 */
    data object Foshan : Campus(2) {
        override val nameRes = Res.string.campus_foshan
        override val schedule get() = foshanSchedule
    }

    companion object {
        /**
         * 通过 [id] 获取校区。
         */
        fun getById(id: Int) = if (id == 1) Canton else Foshan

        /**
         * 通过名字获取校区。
         */
        fun getByName(name: String) = when(name) {
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