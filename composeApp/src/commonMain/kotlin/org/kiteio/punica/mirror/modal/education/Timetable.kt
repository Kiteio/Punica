package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 课表。
 *
 * @property userId 学号
 * @property semester 学期
 * @property createAt 创建时间
 * @property courses 课程，总共含 42 项，每项包含多个同一节次的课程
 * @property note 备注
 */
data class Timetable(
    val userId: String,
    val semester: Semester,
    val createAt: LocalDate,
    val courses: List<List<Course>?>,
    val note: String?,
)