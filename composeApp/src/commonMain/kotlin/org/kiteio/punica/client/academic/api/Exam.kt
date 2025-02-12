package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.serialization.Identifiable

/**
 * 返回考试安排。
 */
suspend fun AcademicSystem.getExams(): Exams {
    val text = get("jsxsd/xsks/xsksap_list").bodyAsText()

    val document = Ksoup.parse(text)
    val tds = document.getElementsByTag("td")

    val exams = mutableListOf<Exam>()
    // 范围排除 Logo
    for (index in 1..<tds.size step 8) {
        exams.add(
            Exam(
                courseId = tds[index + 1].text(),
                courseName = tds[index + 2].text(),
                time = tds[index + 3].text(),
                campus = tds[index + 4].text(),
                classroom = tds[index + 5].text(),
            )
        )
    }

    return Exams(userId, exams)
}


/**
 * 考试安排。
 *
 * @property id 学号
 * @property exams 考试
 */
@Serializable
data class Exams(
    override val id: Long,
    val exams: List<Exam>,
) : Identifiable<Long>


/**
 * 考试安排。
 *
 * @property courseId 课程编号
 * @property courseName 课程名称
 * @property time 时间
 * @property campus 校区
 * @property classroom 考场
 */
@Serializable
data class Exam(
    val courseId: String,
    val courseName: String,
    val time: String,
    val campus: String,
    val classroom: String,
)