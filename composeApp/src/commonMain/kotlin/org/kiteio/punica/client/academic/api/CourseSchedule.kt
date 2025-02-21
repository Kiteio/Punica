package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.foundation.Term
import org.kiteio.punica.serialization.Identifiable

/**
 * 返回学期 [term] 的课程课表。
 */
suspend fun AcademicSystem.getCourseSchedule(term: Term): CourseSchedule {
    val text = submitForm(
        "jsxsd/kbcx/kbxx_kc_ifr",
        parameters { append("xnxqh", "$term") }
    ) { timeout { requestTimeoutMillis = 25000 } }.bodyAsText()

    val doc = Ksoup.parse(text)
    val tds = doc.getElementsByTag("td")
    val regex = Regex("^(.+?)?\\s*\\((.*?)\\)$")

    val courses = mutableListOf<List<CCourse>>()
    // 范围排除 Logo和节次
    for (index in 43..<tds.size step 43) {
        val courseName = tds[index].text()

        val sameCourses = mutableListOf<CCourse>()
        // 范围排除课程名
        for (offset in 1..42) {
            val element = tds[index + offset].child(0)

            // 排除空内容
            if (element.childrenSize() != 0) {
                val divs = element.children()

                for (div in divs) {
                    // 包含上课班级、教师与周次、教室
                    val textNode = div.textNodes()

                    val (teacher, weeksString) = regex.find(textNode[1].text())!!.destructured

                    sameCourses.add(
                        CCourse(
                            name = courseName,
                            teacher = teacher,
                            weeksString = weeksString,
                            weeks = parseWeeksString(weeksString),
                            classroom = textNode[2].text().trim(),
                            sections = ((offset - 1) % 6 * 2).let { setOf(it + 1, it + 2) },
                            dayOfWeek = DayOfWeek((offset - 1) / 6 + 1),
                            clazz = textNode[0].text(),
                        )
                    )
                }
            }
        }
        courses.add(sameCourses)
    }

    return CourseSchedule(term, courses)
}


/**
 * 课程课表。
 *
 * @property id 学期
 * @property courses 课程。每一项是包含多个相同课程的列表。
 */
@Serializable
data class CourseSchedule(
    override val id: Term,
    val courses: List<List<CCourse>>,
) : Identifiable<Term>


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
) : Course