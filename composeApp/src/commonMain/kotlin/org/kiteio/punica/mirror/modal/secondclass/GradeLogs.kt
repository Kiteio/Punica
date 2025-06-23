package org.kiteio.punica.mirror.modal.secondclass

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.kiteio.punica.mirror.modal.education.Semester

/**
 * 成绩日志列表。
 *
 * @property userId 学号
 * @property createAt 创建时间
 * @property logs 成绩日志
 */
data class GradeLogs(
    val userId: String,
    val createAt: LocalDate,
    val logs: List<GradeLog>,
)

/**
 * 成绩日志。
 *
 * @property activityName 活动名称
 * @property category 学分分类
 * @property score 成绩
 * @property time 得分时间
 * @property semester 学期
 */
data class GradeLog(
    val activityName: String,
    val category: String,
    val score: Double,
    val time: LocalDateTime,
    val semester: Semester,
)