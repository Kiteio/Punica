package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 课程课表。
 *
 * @property semester 学期
 * @property createAt 创建时间
 * @property courses 课程，包含多项，每项有一个课程的所有上课信息
 */
data class CourseTable(
    val semester: Semester,
    val createAt: LocalDate,
    val courses: List<List<Course>>
)