package org.kiteio.punica.client.course.api

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Evaluator
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.client.course.CourseSystem

/**
 * 返回选课总览。
 */
suspend fun CourseSystem.getOverview(): Overview {
    return withContext(Dispatchers.Default) {
        val text = get("jsxsd/xsxk/xsxk_tzsm").bodyAsText()

        val doc = Ksoup.parse(text)
        val tds = doc.getElementsByTag("td")

        val rowSize = tds.size / 3
        val progresses = mutableListOf<CreditProgress>()
        for (index in 0..<rowSize) {
            progresses.add(
                CreditProgress(
                    name = tds[index].text(),
                    have = tds[index + rowSize].text(),
                    limit = tds[index + rowSize * 2].text(),
                )
            )
        }

        return@withContext Overview(
            doc.selectFirst(Evaluator.Tag("div"))!!.text(),
            emptyList(),
        )
    }
}


/**
 * 选课总览。
 *
 * @property note 选课轮次信息备注
 * @property progresses 学分进度
 */
class Overview(
    val note: String,
    val progresses: List<CreditProgress>,
)


/**
 * 学分进度。
 *
 * @property name 类别名称
 * @property have 已有学分
 * @property limit 学分限制
 */
class CreditProgress(
    val name: String,
    val have: String,
    val limit: String,
)