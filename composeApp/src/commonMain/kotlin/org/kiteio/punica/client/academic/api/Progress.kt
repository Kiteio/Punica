package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem

/**
 * 返回学业进度。
 */
suspend fun AcademicSystem.getProgresses(): Progresses {
    return withContext(Dispatchers.Default) {
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
                        moduleName = tds[index].text(),
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
                    moduleName = th.textNodes()[0].text().trim(),
                    requiredCredits = tds[tds.lastIndex - 1].text().toDoubleOrNull(),
                    earnedCredits = tds[tds.lastIndex].text().toDoubleOrNull(),
                    progresses = progresses,
                )
            )
        }

        return@withContext Progresses(userId, progressModules)
    }
}


/**
 * 学业进度。
 *
 * @property userId 学号
 * @property modules 学业进度模块
 */
@Serializable
data class Progresses(
    val userId: String,
    val modules: List<ProgressModule>,
)


/**
 * 学业进度模块。
 *
 * @property moduleName 模块
 * @property requiredCredits 模块应修学分
 * @property earnedCredits 已获学分
 * @property progresses 学业进度
 */
@Serializable
data class ProgressModule(
    override val moduleName: String,
    override val requiredCredits: Double?,
    override val earnedCredits: Double?,
    val progresses: List<Progress>,
) : IProgress


/**
 * 学业进度。
 *
 * @property moduleName 课程模块
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
    override val moduleName: String,
    val category: String,
    val courseId: String,
    val courseName: String,
    val credits: Double,
    val termIndex: Int?,
    val note: String?,
    override val requiredCredits: Double?,
    override val earnedCredits: Double?,
) : IProgress


/**
 * 学业进度。
 *
 * @property moduleName 模块名称
 * @property requiredCredits 模块应修学分
 * @property earnedCredits 已获学分
 */
sealed interface IProgress {
    val moduleName: String
    val requiredCredits: Double?
    val earnedCredits: Double?
}