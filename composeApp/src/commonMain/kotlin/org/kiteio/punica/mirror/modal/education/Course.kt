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
 * @property weeksList 周次列表（有序）
 */
data class Course(
    val name: String,
    val teacher: String?,
    val weeks: String,
    val classroom: String,
    val sections: Set<Int>,
    val dayOfWeek: DayOfWeek,
) {
    val weeksList: List<Int> get() = weeks.parseAsWeeksList()

    class Builder {
        var name: String? = null
        var teacher: String? = null
        var weeks: String? = null
        var classroom: String? = null
        var sections: Set<Int>? = null
        var dayOfWeek: DayOfWeek? = null

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
            )
        }
    }
}

/**
 * 解析周次字符串为有序 列表。
 */
fun String.parseAsWeeksList(): List<Int> {
    val list = mutableListOf<Int>()
    var firstNum = ""  // 读取到的第一个数字
    var secondNum = ""  // 读取到的第二个数字
    var isSecond = false  // 正在写入的是否为第二个数

    for (index in indices) {
        val char = this[index]

        if (char.isDigit()) {
            if (isSecond) secondNum += char
            else firstNum += char

            continue
        }

        when (char) {
            // 数字连接符，需要切换为写入第二个数
            '-' -> isSecond = true
            // 数字分隔符，结算此前输入
            ',', '，' -> {
                if (isSecond) {
                    list.addAll(firstNum.toInt()..secondNum.toInt())
                    isSecond = false
                    secondNum = ""
                } else list.add(firstNum.toInt())
                firstNum = ""
            }
            // 跳过空字符、括号
            ' ', '(', '（' -> continue
            // 单双周
            else -> {
                if (isSecond) {
                    val range = firstNum.toInt()..secondNum.toInt()
                    when (char) {
                        '周' -> list.addAll(range)
                        '单' -> list.addAll(range.filter { it % 2 != 0 })
                        '双' -> list.addAll(range.filter { it % 2 == 0 })
                    }
                } else {
                    // 防止出现多余分隔符
                    if (firstNum.isNotEmpty()) list.add(firstNum.toInt())
                }

                break
            }
        }
    }

    return list
}