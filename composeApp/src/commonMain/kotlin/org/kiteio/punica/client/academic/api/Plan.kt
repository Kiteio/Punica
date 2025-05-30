package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.foundation.Term

/**
 * 返回执行计划。
 */
suspend fun AcademicSystem.getPlans(): Plans {
    return withContext(Dispatchers.Default) {
        val text = get("jsxsd/pyfa/pyfa_query").bodyAsText()

        val doc = Ksoup.parse(text)
        val tds = doc.getElementsByTag("td")

        val plans = mutableListOf<Plan>()
        // 范围排除 Logo
        for (index in 1..<tds.size step 10) {
            plans.add(
                Plan(
                    term = Term.parse(tds[index + 1].text()),
                    courseId = tds[index + 2].text(),
                    courseName = tds[index + 3].text(),
                    courseProvider = tds[index + 4].text(),
                    credits = tds[index + 5].text().toDouble(),
                    hours = tds[index + 6].text(),
                    assessmentMethod = tds[index + 7].text(),
                    category = tds[index + 8].text(),
                )
            )
        }

        return@withContext Plans(userId, plans)
    }
}


/**
 * 执行计划。
 *
 * @property userId 学号
 * @property plans 执行计划
 */
@Serializable
data class Plans(
    val userId: String,
    val plans: List<Plan>,
)


/**
 * 执行计划。
 *
 * @property term 学期
 * @property courseId 课程编号
 * @property courseName 课程名称
 * @property courseProvider 开课单位
 * @property credits 学分
 * @property hours 总学时
 * @property assessmentMethod 考核方式
 * @property category 课程属性
 */
@Serializable
data class Plan(
    val term: Term,
    val courseId: String,
    val courseName: String,
    val courseProvider: String,
    val credits: Double,
    val hours: String,
    val assessmentMethod: String,
    val category: String,
)