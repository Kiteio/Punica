package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate

/**
 * 课程课表。
 *
 * @param semester 学期
 * @param createAt 创建时间
 * @param courses 课程，包含多项，每项有一个课程的所有上课信息
 */
data class CourseTable(
    val semester: Semester,
    val createAt: LocalDate,
    val courses: List<List<Course>>
)