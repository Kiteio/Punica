package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 执行计划。
 *
 * @property userId 学号
 *
 * @property plans 教学计划
 */
data class Plans(
    val userId: String,
    val createAt: LocalDate,
    val plans: List<Plan>,
)

/**
 * 教学计划。
 *
 * @property semester 学期
 * @property courseId 课程编号
 * @property courseName 课程名称
 * @property department 开课单位
 * @property credits 学分
 * @property hours 总学时
 * @property assessment 考核方式
 * @property property 课程属性（必修选修）
 */
data class Plan(
    val semester: Semester,
    val courseId: String,
    val courseName: String,
    val department: String,
    val credits: Double,
    val hours: Int,
    val assessment: String,
    val property: String,
)