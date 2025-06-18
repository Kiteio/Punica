package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 课表。
 *
 * @param userId 学号
 * @param createAt 创建时间
 * @param semester 学期
 * @param courses 课程，总共含 42 项，每项包含多个同一节次的课程
 * @param note 备注
 */
data class Timetable(
    val userId: String,
    val createAt: LocalDate,
    val semester: Semester,
    val courses: List<List<Course>?>,
    val note: String?,
)