package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 免听申请列表。
 *
 * @property userId 学号
 * @property createAt 创建时间
 * @property semester 学期
 * @property exemptions 免听申请
 */
data class Exemptions(
    val userId: String,
    val createAt: LocalDate,
    val semester: Semester,
    val exemptions: List<Exemption>,
)

/**
 * 免听申请。
 *
 * @property courseId 课程编号
 * @property courseName 课程名称
 * @property department 开课单位
 * @property teacher 教师
 * @property hours 学时
 * @property credits 学分
 * @property assessment 考核方式
 * @property reason 免听原因
 * @property status 审核状态
 * @property time 申请时间
 */
data class Exemption(
    val courseId: String,
    val courseName: String,
    val department: String,
    val teacher: String,
    val hours: Int,
    val credits: Double,
    val assessment: String,
    val reason: String?,
    val status: String?,
    val time: LocalDate?,
)