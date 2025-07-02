package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.category_basic
import punica.composeapp.generated.resources.category_cross_grade
import punica.composeapp.generated.resources.category_cross_major
import punica.composeapp.generated.resources.category_elective
import punica.composeapp.generated.resources.category_general
import punica.composeapp.generated.resources.category_major

/**
 * 课表课程。
 *
 * @property name 课程名称
 * @property teacher 教师
 * @property weeks 周次
 * @property classroom 教室
 * @property sections 节次
 * @property dayOfWeek 星期
 * @property clazz 上课班级
 */
data class Course(
    val name: String,
    val teacher: String?,
    val weeks: String,
    val classroom: String,
    val sections: Set<Int>,
    val dayOfWeek: DayOfWeek,
    val clazz: String?,
) {
    /**
     * 课表课程 Builder。
     *
     * @property name 课程名称
     * @property teacher 教师
     * @property weeks 周次
     * @property classroom 教室
     * @property sections 节次
     * @property dayOfWeek 星期
     * @property clazz 上课班级
     */
    class Builder {
        var name: String? = null
        var teacher: String? = null
        var weeks: String? = null
        var classroom: String? = null
        var sections: Set<Int>? = null
        var dayOfWeek: DayOfWeek? = null
        var clazz: String? = null

        fun build(): Course {
            require(name != null)
            require(dayOfWeek != null)

            return Course(
                name = name!!,
                teacher = teacher,
                weeks = weeks ?: "",
                classroom = classroom ?: "",
                sections = sections ?: emptySet(),
                dayOfWeek = dayOfWeek!!,
                clazz = clazz,
            )
        }
    }
}

/**
 * 可选课程。
 *
 * @property id 课程唯一标识
 * @property courseId 课程编号
 * @property name 课程名称
 * @property credits 学分
 * @property teacher 教师
 * @property campus 校区
 * @property time 上课时间
 * @property classroom 教师
 * @property note 备注
 * @property conflict 选课冲突情况
 * @property category 课程类别
 * @property isSelectable 是否开放选课
 * @property isSelected 是否已选
 * @property department 开课单位
 * @property assessment 考核方式
 * @property arrangements 上课安排
 * @property total 课程总量
 * @property remain 课程剩余量
 */
data class SelectableCourse(
    val id: String,
    val courseId: String,
    val name: String,
    val credits: Double,
    val teacher: String,
    val campus: Campus,
    val time: String?,
    val classroom: String?,
    val note: String?,
    val conflict: String?,
    val category: Category,
    val isSelectable: Boolean,
    val isSelected: Boolean,
    val department: String,
    val assessment: String,
    val arrangements: List<Arrangement>?,
    val total: Int,
    val remain: Int,
) {
    /**
     * 课程上课安排。
     *
     * @property classroom 教室
     * @property weeks 周次
     * @property dayOfWeek 星期
     * @property sections 节次
     */
    data class Arrangement(
        val classroom: String,
        val weeks: String,
        val dayOfWeek: DayOfWeek,
        val sections: Set<Int>,
    )

    /**
     * 课程类别。
     *
     * @property routeSegment 路由片段。默认首字母大写，用于搜索，若用于选课需转为小写
     */
    sealed class Category {
        abstract val routeSegment: String
        abstract val nameRes: StringResource

        sealed class Special(
            override val routeSegment: String,
            override val nameRes: StringResource,
        ) : Category() {
            /** 学科基础、专业必修课 */
            data object Basic : Special("Bx", Res.string.category_basic)

            /** 选修课 */
            data object Elective : Special("Xx", Res.string.category_elective)
        }

        sealed class Common(
            override val routeSegment: String,
            override val nameRes: StringResource,
        ) : Category() {
            /** 通识课 */
            data object General : Common("Ggxxk", Res.string.category_general)

            /** 专业内计划课 */
            data object Program : Common("Bxqjh", Res.string.category_major)

            /** 跨年级 */
            data object CrossGrade : Common("Knj", Res.string.category_cross_grade)

            /** 跨专业 */
            data object CrossMajor : Common("Faw", Res.string.category_cross_major)
        }
    }

    /**
     * 选课优先级。
     */
    sealed class Priority(val value: Int) {
        /** 第一志愿 */
        data object First : Priority(1)

        /** 第二志愿 */
        data object Second : Priority(2)

        /** 第三志愿 */
        data object Third : Priority(3)
    }

    /**
     * 搜索参数。
     *
     * @property name 课程名称
     * @property teacher 教师
     * @property dayOfWeek 星期
     * @property sectionPair 节次
     * @property campus 校区
     * @property filterFull 是否过滤无剩余量
     * @property filterConflict 是否过滤冲突课程
     */
    data class Parameters(
        val name: String = "",
        val teacher: String = "",
        val dayOfWeek: DayOfWeek? = null,
        val sectionPair: SectionPair? = null,
        val campus: Campus? = null,
        val filterFull: Boolean = false,
        val filterConflict: Boolean = false,
    )
}

/**
 * 已选课程。
 *
 * @property id 课程唯一标识
 * @property courseId 课程编号
 * @property name 课程名称
 * @property credits 学分
 * @property category 课程类别
 * @property teacher 教师
 * @property time 上课时间。格式为“周次 星期 节次”
 * @property classroom
 */
data class SelectedCourse(
    val id: String,
    val courseId: String,
    val name: String,
    val credits: Double,
    val category: String,
    val teacher: String,
    val time: String?,
    val classroom: String?,
) {
    /**
     * 转化为 [Course]。
     */
    fun asCourse(): Course {
        check(time != null)

        val timeSegment = time.split(" ")
        return Course(
            name = name,
            teacher = teacher,
            weeks = timeSegment[0],
            classroom = classroom ?: "",
            sections = timeSegment[2].let { section ->
                Regex("\\d+")
                    .findAll(section)
                    .map { it.value.toInt() }
                    .toList().sorted()
                    // 有些多节次的课只会显示开始和结束，如 1-4 节，而不是 1-2-3-4 节
                    .let { it[0]..it[1] }.toSet()
            },
            dayOfWeek = timeSegment.get(1).let {
                DayOfWeek(
                    when (it) {
                        "星期一" -> 1
                        "星期二" -> 2
                        "星期三" -> 3
                        "星期四" -> 4
                        "星期五" -> 5
                        "星期六" -> 6
                        else -> 7
                    }
                )
            },
            clazz = null,
        )
    }
}

/**
 * 课程上课周次是否包含 [week]。
 */
fun Course.containsWeek(week: Int): Boolean {
    var firstNumber = ""
    var secondNumber = ""
    var isSecond = false

    for (index in weeks.indices) {
        val char = weeks[index]

        // 读取数字
        if (char.isDigit()) {
            if (isSecond) {
                secondNumber += char
            } else {
                firstNumber += char
            }
            continue
        }

        when (char) {
            // 跳过空字符、括号
            ' ', '(', '（' -> continue
            // 遇到连字符将读取的数字写入 secondNumber
            '-' -> isSecond = true
            // 周次分隔
            ',', '，' -> {
                if (isSecond) {
                    if (week in firstNumber.toInt()..secondNumber.toInt()) {
                        return true
                    }
                    isSecond = false
                    secondNumber = ""
                    firstNumber = ""
                } else {
                    val firstWeek = firstNumber.toInt()
                    when {
                        // 因为周次是有序的，若小于第一个，则小于全部
                        week < firstWeek -> return false
                        week == firstWeek -> return true
                    }
                    firstNumber = ""
                }
            }

            // 单、双、周
            else -> {
                if (isSecond) {
                    val range = firstNumber.toInt()..secondNumber.toInt()
                    return when (char) {
                        '周' -> range.contains(week)
                        '单' -> week % 2 != 0 && range.contains(week)
                        '双' -> week % 2 == 0 && range.contains(week)
                        else -> continue
                    }
                } else {
                    if (firstNumber.isNotEmpty() && week == firstNumber.toInt()) {
                        return true
                    }
                }

                break
            }
        }
    }
    return false
}