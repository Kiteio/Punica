package org.kiteio.punica.mirror.service

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.AmPmMarker
import org.kiteio.punica.mirror.modal.cet.CetExam
import org.kiteio.punica.mirror.modal.cet.CetExamTime
import org.kiteio.punica.mirror.platform.platformHttpClient

/**
 * 四六级服务。
 */
@Singleton
fun getCetService(): CetService {
    val httpClient = platformHttpClient {
        defaultRequest {
            url(CetServiceImpl.BASE_URL)
        }
    }

    return CetServiceImpl(httpClient)
}

/**
 * 四六级服务。
 */
interface CetService {
    /**
     * 获取 Cet 考试。
     */
    suspend fun getExam(): CetExam
}

// --------------- 实现 ---------------

private class CetServiceImpl(
    private val httpClient: HttpClient,
) : CetService {
    override suspend fun getExam() = withContext(Dispatchers.Default) {
        val text = httpClient.get("/project/CET/IndexEdu.css")
            .bodyAsText()

        val doc = Ksoup.parse(text)
        val divs = doc.getElementsByClass("main_info_l")

        val firstDiv = divs.first()!!
        val ul = firstDiv.child(1)
        val spans = ul.child(4).children()

        val name = firstDiv.child(0).text().removeSuffix("：")
        val year = name.substring(0..3).toInt()
        // 笔试日期
        val writtenDate = ul.child(0).text()
            .split("：")[1]
                .let { LocalDate.parse("$year-$it") }
        // 四级口语日期
        val cet4SpeakingDate = spans[0].text()
            .split("：")[0]
                .let { LocalDate.parse("$year-$it") }
        // 六级口语日期
        val cet6SpeakingDate = spans[1].text()
            .split("：")[0]
                .let { LocalDate.parse("$year-$it") }

        return@withContext CetExam(
            name = name,
            cet4Written = CetExamTime(
                writtenDate,
                AmPmMarker.AM,
            ),
            cet6Written = CetExamTime(
                writtenDate,
                AmPmMarker.PM,
            ),
            cet4Speaking = CetExamTime(
                cet4SpeakingDate,
            ),
            cet6Speaking = CetExamTime(
                cet6SpeakingDate,
            ),
            note = divs[1].text(),
            pdfUrlString = "$BASE_URL/project/CET/News/TestDataPlan-CET.pdf",
        )
    }

    companion object {
        const val BASE_URL = "https://resource.neea.edu.cn"
    }
}