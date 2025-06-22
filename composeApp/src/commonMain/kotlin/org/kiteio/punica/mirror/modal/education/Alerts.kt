package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 学籍预警列表。
 *
 * @property userId 学号
 * @property createAt 创建时间
 * @property alerts 学籍预警列表
 * @property pageCount 总页数
 */
data class Alerts(
    val userId: String,
    val createAt: LocalDate,
    val alerts: List<Alert>,
    val pageCount: Int,
)

/**
 * 学籍预警。
 *
 * @property semester 学期
 * @property importance 紧急程度
 * @property description 描述
 * @property value 描述值
 */
data class Alert(
    val semester: Semester,
    val importance: Importance,
    val description: String,
    val value: String,
) {
    /**
     * 紧急程度。
     */
    sealed class Importance {
        /** 红色预警 */
        data object High : Importance()

        /** 黄色预警 */
        data object Medium : Importance()

        /** 蓝色预警 */
        data object Low : Importance()

        /** 未知 */
        data object Unknown : Importance()
    }
}