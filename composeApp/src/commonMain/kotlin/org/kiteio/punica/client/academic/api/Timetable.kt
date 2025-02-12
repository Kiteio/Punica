package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.foundation.Term
import org.kiteio.punica.serialization.Identifiable

/**
 * 返回学期 [term] 的课表。
 */
suspend fun AcademicSystem.getTimetable(term: Term): Timetable {
    val text = get("jsxsd/xskb/xskb_list.do") {
        parameter("xnxq01id", term)
    }.bodyAsText()

    val doc = Ksoup.parse(text)
    val tds = doc.getElementsByTag("td").apply { removeFirst() }

    val cells = mutableListOf<MutableList<Course>?>()

    // 从上至下逐列获取、排序
    for (column in 0..6) for (row in 0..5) {
        // <div class="kbcontent"></div>
        val div = tds[row * 7 + column].child(3)

        if (div.childrenSize() > 0) {
            // 包含多个课程的名称、节次、分割线
            val textNodes = div.textNodes()
            // 包含多个课程的教师、周次、教室
            val parts = div.html().split("---------------------")

            val cell = mutableListOf<Course>()
            for (partIndex in parts.indices) {
                // 正则解析节次，如“[01-02]节”
                val section = Regex("\\d+")
                    .findAll(textNodes[partIndex * 3 + 1].text())
                    .map { it.value.toInt() }
                    .toSet()

                // 排除课程节次大于 2 的第二个课程，节次如“[01-02-03]节”、“[01-02-03-04]节”
                if (section.size > 2 && row % 2 != 0) continue

                val fonts = Ksoup.parse(parts[partIndex]).getElementsByTag("font")

                var teacher = ""
                val weeks = mutableSetOf<Int>()
                var classroom = ""

                // 由 font 获取上课信息
                for (font in fonts) {
                    val fontText = font.text()

                    // <font title="[类型]"></font>
                    when (font.attr("title")) {
                        "老师" -> teacher = fontText
                        "周次(节次)" -> weeks.addAllWeeks(fontText)
                        "教室" -> classroom = fontText
                    }
                }

                cell.add(
                    MCourse(
                        name = textNodes[partIndex * 3].text(),
                        teacher = teacher.ifEmpty { null },
                        weeks = weeks,
                        classroom = classroom.ifEmpty { null },
                        sections = section,
                        dayOfWeek = DayOfWeek(column + 1),
                    )
                )
            }
            cells.add(cell.takeIf { it.size > 0 })
        } else cells.add(null)
    }

    return Timetable(userId, term, tds[tds.lastIndex].text().takeIf { it != "未安排时间课程：" }, cells)
}


/**
 * 课表。
 *
 * @property userId 学号
 * @property term 学期
 * @property note 备注
 * @property cells 课表项
 */
@Serializable
data class Timetable(
    val userId: Long,
    val term: Term,
    val note: String?,
    val cells: List<List<Course>?>,
) : Identifiable<String> {
    /** [userId] + [term] */
    override val id = "$userId$term"
}


/**
 * 课程。
 *
 * @property name 名称
 * @property teacher 教师
 * @property weeks 周次
 * @property classroom 教室
 * @property sections 节次
 * @property dayOfWeek 星期
 */
interface Course {
    val name: String
    val teacher: String?
    val weeks: Set<Int>
    val classroom: String?
    val sections: Set<Int>
    val dayOfWeek: DayOfWeek
}


/**
 * 课表课程。
 */
@Serializable
data class MCourse(
    override val name: String,
    override val teacher: String?,
    override val weeks: Set<Int>,
    override val classroom: String?,
    override val sections: Set<Int>,
    override val dayOfWeek: DayOfWeek,
) : Course


/**
 * 将 [weeksStr] 解析解析为 [Int] 并添加至 [MutableList]。
 */
fun MutableSet<Int>.addAllWeeks(weeksStr: String) {
    var firstNum = ""  // 读取到的第一个数字
    var secondNum = ""  // 读取到的第二个数字
    var isSecond = false  // 正在写入的是否为第二个数

    for (index in weeksStr.indices) {
        val char = weeksStr[index]

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
                    addAll(firstNum.toInt()..secondNum.toInt())
                    isSecond = false
                    secondNum = ""
                } else add(firstNum.toInt())
                firstNum = ""
            }
            // 跳过空字符、括号
            ' ', '(', '（' -> continue
            // 单双周
            else -> {
                if (isSecond) {
                    val range = firstNum.toInt()..secondNum.toInt()
                    when (char) {
                        '周' -> addAll(range)
                        '单' -> addAll(range.filter { it % 2 != 0 })
                        '双' -> addAll(range.filter { it % 2 == 0 })
                    }
                } else {
                    // 防止出现多余分隔符
                    if (firstNum.isNotEmpty()) add(firstNum.toInt())
                }

                break
            }
        }
    }
}