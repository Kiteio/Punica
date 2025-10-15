package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator
import org.jetbrains.compose.resources.getString
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.mirror.util.AppDirs
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.app_name
import punica.composeapp.generated.resources.graduation

/**
 * 毕业审查。
 */
suspend fun AcademicSystem.getGraduation(isPrimary: Boolean = true): Graduation {
    return withContext(Dispatchers.Default) {
        val options = Ksoup.parse(
            get("jsxsd/bygl/bybm").bodyAsText(),
        ).getElementById("bybm")!!.children().apply { sortBy { it.text() } }
        // 主修（lastIndex - 1）或辅修（lastIndex）
        val value = if (isPrimary) options[options.lastIndex - 1].value() else
            options[options.lastIndex].value()

        val doc = Ksoup.parse(
            submitForm(
                "/jsxsd/bygl/bybmcz.do",
                parameters { append("bybm", value) },
            ).bodyAsText(),
        )

        val tds = doc.getElementsByTag("td")
        val docUrl = Regex(
            "\\(['\"]([^'\"]*)['\"]\\)",
        ).find(
            tds[9]
                .getElementsByTag("a")
                .attr("href"),
        )!!.groupValues[1]
        return@withContext Graduation(
            year = tds[1].text().toInt(),
            name = tds[2].text(),
            type = tds[3].text(),
            method = tds[4].text(),
            credits = tds[5].text().toDouble(),
            completionRate = tds[6].text().toInt(),
            enrolmentRate = tds[7].text().toInt(),
            note = tds[8].text().ifBlank { null },
            docUrl = docUrl,
        )
    }
}

/**
 * 下载毕业审核报告。
 */
suspend fun AcademicSystem.downloadGraduationReport(route: String): Path {
    val dir = AppDirs.downloadsDir(getString(Res.string.app_name))
    val path = Path(
        "$dir${SystemPathSeparator}$userId${getString(Res.string.graduation)}.doc",
    )
    // 获取并写入训练数据
    SystemFileSystem.createDirectories(Path(dir))
    val bytes = get(route) {
        timeout { requestTimeoutMillis = 20000 }
    }.readRawBytes()
    SystemFileSystem.sink(path).buffered().use {
        it.write(bytes)
    }
    return path
}

/**
 * 毕业信息。
 *
 * @property year 年份
 * @property name 批次名称
 * @property type 审核类别
 * @property method 报名方式
 * @property credits 学位成绩绩点
 * @property completionRate 结业学分比率
 * @property enrolmentRate 报名学分比率
 * @property note 备注
 * @property docUrl 毕业审核报告
 */
data class Graduation(
    val year: Int,
    val name: String,
    val type: String,
    val method: String,
    val credits: Double,
    val completionRate: Int,
    val enrolmentRate: Int,
    val note: String? = null,
    val docUrl: String,
)