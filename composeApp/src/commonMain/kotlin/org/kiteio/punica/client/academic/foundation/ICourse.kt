package org.kiteio.punica.client.academic.foundation

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

/**
 * 课程。
 *
 * @property name 名称
 * @property teacher 教师
 * @property weeksString 周次字符串
 * @property weeks 周次
 * @property classroom 教室
 * @property sections 节次
 * @property dayOfWeek 星期
 */
@Serializable
sealed interface ICourse {
    val name: String
    val teacher: String?
    val weeksString: String
    val weeks: Set<Int>
    val classroom: String?
    val sections: Set<Int>
    val dayOfWeek: DayOfWeek
}


/**
 * 课表课程。
 */
@Serializable
data class Course(
    override val name: String,
    override val teacher: String?,
    override val weeksString: String,
    override val weeks: Set<Int>,
    override val classroom: String?,
    override val sections: Set<Int>,
    override val dayOfWeek: DayOfWeek,
) : ICourse


/**
 * 课程。
 *
 * @property clazz 班级
 */
@Serializable
data class CCourse(
    override val name: String,
    override val teacher: String?,
    override val weeksString: String,
    override val weeks: Set<Int>,
    override val classroom: String?,
    override val sections: Set<Int>,
    override val dayOfWeek: DayOfWeek,
    val clazz: String,
) : ICourse