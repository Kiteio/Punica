package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 等级成绩列表。
 *
 * @property userId 学号
 * @property createAt 创建时间
 * @property grades 等级考试成绩
 */
data class LevelGrades(
    val userId: String,
    val createAt: LocalDate,
    val grades: List<LevelGrade>,
)

/**
 * 等级成绩。
 *
 * @property name 考试名称
 * @property score 成绩
 * @property date 考试时间
 */
data class LevelGrade(
    val name: String,
    val score: String,
    val date: LocalDate,
)