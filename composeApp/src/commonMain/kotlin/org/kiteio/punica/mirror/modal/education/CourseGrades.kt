package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 课程成绩列表。
 *
 * @property userId 学号
 * @property createAt 创建时间
 * @property overview 概览
 * @property grades 课程成绩
 */
data class CourseGrades(
    val userId: String,
    val createAt: LocalDate,
    val overview: String,
    val grades: List<CourseGrade>,
)

/**
 * 课程成绩。
 *
 * @property semester 学期
 * @property courseId 课程编号
 * @property name 课程名称
 * @property usualScore 平时成绩
 * @property labScore 实验成绩
 * @property finalScore 期末成绩
 * @property score 成绩
 * @property credits 学分
 * @property hours 学时
 * @property assessment 考核方式
 * @property property 课程属性（必修、选修）
 * @property category 课程性质（专业课、学科基础课、通识课）
 * @property electiveCategory 通选课分类
 * @property examCategory 考试性质
 * @property mark 成绩标识
 * @property note 备注
 */
data class CourseGrade(
    val semester: Semester,
    val courseId: String,
    val name: String,
    val usualScore: String?,
    val labScore: String?,
    val finalScore: String?,
    val score: String,
    val credits: Double,
    val hours: Int,
    val assessment: String,
    val property: String,
    val category: String,
    val electiveCategory: String?,
    val examCategory: String,
    val mark: String?,
    val note: String?,
) {
    /** 绩点 */
    val points: Double
        get() = when {
            score.all { it.isDigit() } -> score.toDouble().let {
                // 90 - 100 -> 4.0 - 5.0
                // 80 - 90 -> 3.0 - 3.9
                // 70 - 80 -> 2.0 - 2.9
                // 60 - 70 -> 1.0 - 1.9
                // 60 以下 -> 0.0
                if(it >= 60) ( score.toDouble() - 50) / 10 else 0.0
            }

            else -> when (score) {
                "优" -> 4.5
                "良" -> 3.5
                "中" -> 2.5
                "及格" -> 1.5
                // 不及格
                else -> 0.0
            }
        }
}