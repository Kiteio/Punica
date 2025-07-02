package org.kiteio.punica.mirror.modal.education

/**
 * 退课日志。
 *
 * @property courseId 课程编号
 * @property courseName 课程名称
 * @property credits 学分
 * @property property 课程属性
 * @property teacher 教师
 * @property time 上课时间
 * @property category 课程类别
 * @property action 退课类型
 * @property operateTime 操作时间
 * @property operator 操作者
 * @property description 操作说明
 */
data class CourseLog(
    val courseId: String,
    val courseName: String,
    val credits: Double,
    val property: String,
    val teacher: String,
    val time: List<String>,
    val category: String,
    val action: String,
    val operateTime: String,
    val operator: String,
    val description: String,
)