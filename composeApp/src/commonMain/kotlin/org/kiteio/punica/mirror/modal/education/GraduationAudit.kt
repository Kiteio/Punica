package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 毕业审核。
 *
 * @property userId 学号
 * @property createAt 创建时间
 * @property year 年份
 * @property name 批次名称
 * @property category 审核类别
 * @property channel 报名方式
 * @property credits 学位成绩绩点
 * @property completionRate 结业学分比率
 * @property enrolmentRate 报名学分比率
 * @property note 备注
 * @property reportUrl 毕业审核报告
 */
data class GraduationAudit(
    val userId: String,
    val createAt: LocalDate,
    val year: Int,
    val name: String,
    val category: String,
    val channel: String,
    val credits: Double,
    val completionRate: Double,
    val enrolmentRate: Double,
    val note: String?,
    val reportUrl: String,
)