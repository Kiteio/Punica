package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.serialization.Identifiable

/**
 * 返回考试安排。
 */
suspend fun AcademicSystem.getExams(): Exams {
    val text = get("jsxsd/xsks/xsksap_list").bodyAsText()

    val doc = Ksoup.parse(text)
    val tds = doc.getElementsByTag("td")
    // yyyy-MM-dd HH:mm
    val formatter = LocalDateTime.Format {
        year(); char('-')
        monthNumber(); char('-')
        dayOfMonth(); char(' ')
        hour(); char(':')
        minute()
    }

    val exams = mutableListOf<Exam>()
    // 范围排除 Logo
    for (index in 1..<tds.size step 8) {
        exams.add(
            Exam(
                courseId = tds[index + 1].text(),
                courseName = tds[index + 2].text(),
                duration = tds[index + 3].text().split("~").let {
                    LocalDateTime.parse(it[0], formatter)..LocalDateTime.parse(it[1])
                },
                campus = if (tds[index + 4].text() == "广州校区") Campus.CANTON else Campus.FO_SHAN,
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
 * @property duration 持续时间
 * @property campus 校区
 * @property classroom 考场
 */
@Serializable
data class Exam(
    val courseId: String,
    val courseName: String,
    val duration: ClosedRange<LocalDateTime>,
    val campus: Campus,
    val classroom: String,
)