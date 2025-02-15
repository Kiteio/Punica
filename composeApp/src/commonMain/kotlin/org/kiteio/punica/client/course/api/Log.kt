package org.kiteio.punica.client.course.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import org.kiteio.punica.client.course.CourseSystem

/**
 * 返回退课日志。
 */
suspend fun CourseSystem.getWithdrawalLogs() {
    val text = get("jsxsd/xsxkjg/getTkrzList").bodyAsText()

    val doc = Ksoup.parse(text)
    val tds = doc.getElementsByTag("td")

    val withdrawalLogs = mutableListOf<WithdrawalLog>()
    for (index in tds.indices step 11) {
        withdrawalLogs.add(
            WithdrawalLog(
                courseId = tds[index].text(),
                courseName = tds[index + 1].text(),
                credits = tds[index + 2].text().toDouble(),
                courseCategory = tds[index + 3].text(),
                courseTeacher = tds[index + 4].text(),
                courseTime = tds[index + 5].run {
                    if(text().isBlank()) emptyList()
                    else textNodes().map { textNode -> textNode.text() }
                },
                courseType = tds[index + 6].text(),
                operationType = tds[index + 7].text(),
                time = tds[index + 8].text(),
                operator = tds[index + 9].text(),
                description = tds[index + 10].text(),
            )
        )
    }
}


/**
 * 退课日志。
 *
 * @property courseId 课程编号
 * @property courseName 课程名称
 * @property credits 学分
 * @property courseCategory 课程属性
 * @property courseTeacher 教师
 * @property courseTime 上课时间
 * @property courseType 选课分类
 * @property operationType 退课类型
 * @property time 时间
 * @property operator 操作者
 * @property description 操作说明
 */
class WithdrawalLog(
    val courseId: String,
    val courseName: String,
    val credits: Double,
    val courseCategory: String,
    val courseTeacher: String,
    val courseTime: List<String>,
    val courseType: String,
    val operationType: String,
    val time: String,
    val operator: String,
    val description: String,
)