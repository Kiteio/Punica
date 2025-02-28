package org.kiteio.punica.client.academic.foundation

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.api.parseWeeksString

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


/**
 * 已选课程。
 *
 * @property courseId 课程编号
 * @property name 课程名称
 * @property credits 学分
 * @property category 课程属性
 * @property teacher 教师
 * @property time 上课时间
 * @property classroom 教师
 * @property id 操作 id
 */
data class MCourse(
    val courseId: String,
    override val name: String,
    val credits: Double,
    val category: String,
    override val teacher: String,
    val time: String,
    override val classroom: String,
    val id: String,
) : ICourse {
    private val timeSplits = time.split(" ")

    override val weeksString = timeSplits[0]

    override val weeks = parseWeeksString(weeksString)

    override val sections = Regex("\\d+")
        .findAll(timeSplits[2])
        .map { it.value.toInt() }
        .toSet()

    override val dayOfWeek = DayOfWeek(
        when (timeSplits[1]) {
            "星期一" -> 1
            "星期二" -> 2
            "星期三" -> 3
            "星期四" -> 4
            "星期五" -> 5
            "星期六" -> 6
            else -> 7
        }
    )
}