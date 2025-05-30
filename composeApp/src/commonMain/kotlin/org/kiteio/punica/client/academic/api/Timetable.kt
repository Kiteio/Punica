package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.foundation.Course
import org.kiteio.punica.client.academic.foundation.Term

/**
 * 返回学期 [term] 的课表。
 */
suspend fun AcademicSystem.getTimetable(term: Term): Timetable {
    return withContext(Dispatchers.Default) {
        val text = get("jsxsd/xskb/xskb_list.do") {
            parameter("xnxq01id", term)
        }.bodyAsText()

        val doc = Ksoup.parse(text)
        val tds = doc.getElementsByTag("td").apply { removeAt(0) }

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
                    var weeksString = ""
                    var classroom = ""

                    // 由 font 获取上课信息
                    for (font in fonts) {
                        val fontText = font.text()

                        // <font title="[类型]"></font>
                        when (font.attr("title")) {
                            "老师" -> teacher = fontText
                            "周次(节次)" -> weeksString = fontText
                            "教室" -> classroom = fontText
                        }
                    }

                    cell.add(
                        Course(
                            name = textNodes[partIndex * 3].text(),
                            teacher = teacher.ifEmpty { null },
                            weeksString = weeksString,
                            weeks = parseWeeksString(weeksString),
                            classroom = classroom.ifEmpty { null },
                            sections = section,
                            dayOfWeek = DayOfWeek(column + 1),
                        )
                    )
                }
                cells.add(cell.takeIf { it.isNotEmpty() })
            } else cells.add(null)
        }

        return@withContext Timetable(
            userId,
            term,
            tds[tds.lastIndex].text().takeIf { it != "未安排时间课程：" },
            cells,
        )
    }
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
    val userId: String,
    val term: Term,
    val note: String?,
    val cells: List<List<Course>?>,
) {
    /** [userId] + [term] */
    val id = "$userId$term"
}


/**
 * 返回 [text] 解析后 [Set]<[Int]>。
 */
fun parseWeeksString(text: String): Set<Int> {
    val set = mutableSetOf<Int>()
    var firstNum = ""  // 读取到的第一个数字
    var secondNum = ""  // 读取到的第二个数字
    var isSecond = false  // 正在写入的是否为第二个数

    for (index in text.indices) {
        val char = text[index]

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
                    set.addAll(firstNum.toInt()..secondNum.toInt())
                    isSecond = false
                    secondNum = ""
                } else set.add(firstNum.toInt())
                firstNum = ""
            }
            // 跳过空字符、括号
            ' ', '(', '（' -> continue
            // 单双周
            else -> {
                if (isSecond) {
                    val range = firstNum.toInt()..secondNum.toInt()
                    when (char) {
                        '周' -> set.addAll(range)
                        '单' -> set.addAll(range.filter { it % 2 != 0 })
                        '双' -> set.addAll(range.filter { it % 2 == 0 })
                    }
                } else {
                    // 防止出现多余分隔符
                    if (firstNum.isNotEmpty()) set.add(firstNum.toInt())
                }

                break
            }
        }
    }

    return set
}