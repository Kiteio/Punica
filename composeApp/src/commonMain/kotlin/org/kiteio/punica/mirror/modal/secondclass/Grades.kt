package org.kiteio.punica.mirror.modal.secondclass

import kotlinx.datetime.LocalDate

/**
 * 第二课堂成绩单。
 *
 * @property userId 学号
 * @property createAt 创建时间
 * @property grades 成绩列表
 */
data class Grades(
    val userId: String,
    val createAt: LocalDate,
    val grades: List<Grade>,
)

/**
 * 第二课堂成绩。
 *
 * @property name 分类名称
 * @property score 分数
 * @property requiredScore 要求分数
 */
data class Grade(
    val name: String,
    val score: Double,
    val requiredScore: Double,
)