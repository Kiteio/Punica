package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.serialization.Identifiable

/**
 * 返回学业进度。
 */
suspend fun AcademicSystem.getProgresses(): Progresses {
    val text = submitForm(
        "jsxsd/pyfa/xyjdcx",
        parameters { append("xdlx", "0") },
    ) { parameter("type", "cx") }.bodyAsText()

    val doc = Ksoup.parse(text)
    val tables = doc.getElementsByTag("tbody")

    val progressModules = mutableListOf<ProgressModule>()
    // 范围排除 Logo
    for (tableIndex in 1..<tables.size) {
        // 获取每个表格的进度
        val tds = tables[tableIndex].getElementsByTag("td")

        val progresses = mutableListOf<Progress>()
        // 范围排除末尾合计
        for (index in 0..<tds.size - 3 step 9) {
            progresses.add(
                Progress(
                    module = tds[index].text(),
                    category = tds[index + 1].text(),
                    courseId = tds[index + 2].text(),
                    courseName = tds[index + 3].text(),
                    credits = tds[index + 4].text().toDouble(),
                    termIndex = tds[index + 5].text().toIntOrNull(),
                    note = tds[index + 6].text(),
                    requiredCredits = tds[index + 7].text().toDoubleOrNull(),
                    earnedCredits = tds[index + 8].text().toDoubleOrNull(),
                )
            )
        }

        val th = tables[tableIndex].child(0).child(0)
        progressModules.add(
            ProgressModule(
                name = th.textNodes()[0].text().trim(),
                earnedCredits = tds[tds.lastIndex - 1].text().toDoubleOrNull(),
                requiredCredits = tds[tds.lastIndex - 1].text().toDoubleOrNull(),
                progresses = progresses,
            )
        )
    }

    return Progresses(userId, progressModules)
}


/**
 * 学业进度。
 *
 * @property id 学号
 * @property modules 学业进度模块
 */
@Serializable
data class Progresses(
    override val id: Long,
    val modules: List<ProgressModule>,
) : Identifiable<Long>


/**
 * 学业进度模块。
 *
 * @property name 模块
 * @property earnedCredits 已获学分
 * @property requiredCredits 模块应修学分
 * @property progresses 学业进度
 */
@Serializable
data class ProgressModule(
    val name: String,
    val earnedCredits: Double?,
    val requiredCredits: Double?,
    val progresses: List<Progress>,
)


/**
 * 学业进度。
 *
 * @property module 课程模块
 * @property category 课程属性（教务系统中称其为“性质”，与课程成绩中的说法矛盾）
 * @property courseId 课程代码
 * @property courseName 课程名称
 * @property credits 学分
 * @property termIndex 建议修读学期
 * @property note 免听、免修
 * @property requiredCredits 模块应修学分
 * @property earnedCredits 已获学分
 */
@Serializable
data class Progress(
    val module: String,
    val category: String,
    val courseId: String,
    val courseName: String,
    val credits: Double,
    val termIndex: Int?,
    val note: String?,
    val requiredCredits: Double?,
    val earnedCredits: Double?,
)