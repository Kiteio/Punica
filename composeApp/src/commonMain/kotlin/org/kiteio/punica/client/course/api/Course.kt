package org.kiteio.punica.client.course.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.client.course.CourseSystem

/**
 * 返回已选课程列表。
 */
suspend fun CourseSystem.getCourses(): List<MCourse> {
    return withContext(Dispatchers.Default) {
        val text = get("jsxsd/xsxkjg/comeXkjglb").bodyAsText()

        val doc = Ksoup.parse(text)
        val tds = doc.getElementsByTag("td")

        val courses = mutableListOf<MCourse>()
        for (index in tds.indices step 9) {
            courses.add(
                MCourse(
                    courseId = tds[index].text(),
                    name = tds[index + 1].text(),
                    credits = tds[index + 2].text().toDouble(),
                    category = tds[index + 3].text(),
                    teacher = tds[index + 4].text(),
                    time = tds[index + 5].text(),
                    classroom = tds[index + 6].text(),
                    id = tds[index + 8].text(),
                )
            )
        }
        return@withContext courses
    }
}


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
    val name: String,
    val credits: Double,
    val category: String,
    val teacher: String,
    val time: String,
    val classroom: String,
    val id: String,
)