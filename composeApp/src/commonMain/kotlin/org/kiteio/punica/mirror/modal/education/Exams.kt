package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * 考试。
 *
 * @property userId 学号
 * @property createAt 创建时间
 * @property exams 考试
 */
data class Exams(
    val userId: String,
    val createAt: LocalDate,
    val exams: List<Exam>,
)

/**
 * 考试项。
 *
 * @property courseId 课程编号
 * @property courseName 课程名称
 * @property duration 考试时间
 * @property campus 校区
 * @property classroom 考场
 */
data class Exam(
    val courseId: String,
    val courseName: String,
    val duration: ClosedRange<LocalDateTime>,
    val campus: Campus,
    val classroom: String,
)