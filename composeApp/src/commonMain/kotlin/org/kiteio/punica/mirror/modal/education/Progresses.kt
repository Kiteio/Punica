package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 学业进度。
 *
 * @property userId 学号
 * @property createAt 创建时间
 * @property modules 模块进度
 */
data class Progresses(
    val userId: String,
    val createAt: LocalDate,
    val modules: List<ProgressModule>
)

/**
 * 模块进度。
 *
 * @property name 模块名称
 * @property requiredCredits 模块应修学分
 * @property earnedCredits 已获学分
 * @property progresses 课程进度
 */
data class ProgressModule(
    val name: String,
    val requiredCredits: Double?,
    val earnedCredits: Double?,
    val progresses: List<Progress>
)

/**
 * 课程进度。
 * 
 * @property name 模块名称
 * @property property 课程属性（必修选修）
 * @property courseId 课程编号
 * @property courseName 课程名称
 * @property credits 学分
 * @property termIndex 建议修读学期
 * @property privilege 免听、免修
 * @property requiredCredits 模块应修学分
 * @property earnedCredits 已获学分
 */
data class Progress(
    val name: String,
    val property: String,
    val courseId: String,
    val courseName: String,
    val credits: Double,
    val termIndex: Int?,
    val privilege: String?,
    val requiredCredits: Double?,
    val earnedCredits: Double?,
)