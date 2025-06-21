package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.DayOfWeek

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