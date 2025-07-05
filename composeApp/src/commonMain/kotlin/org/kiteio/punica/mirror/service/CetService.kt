package org.kiteio.punica.mirror.service

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.mirror.modal.cet.CetExams
import org.kiteio.punica.mirror.platform.platformHttpClient

/**
 * 四六级服务。
 */
fun CetService(): CetService {
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
    suspend fun getExam(): CetExams
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

        val writtenTime = ul.child(0).text()
            .replace("笔试：", "")
        val amSuject = ul.child(1).text()
        val pmSuject = ul.child(2).text()
        val writtenSubject = "$amSuject\n${pmSuject}"

        val spans = ul.child(4).children()

        return@withContext CetExams(
            name = firstDiv.child(0).text(),
            written = mapOf(writtenTime to writtenSubject),
            speaking = mapOf(
                spans[0].text().split("：")
                    .let { it[0] to it[1] },
                spans[1].text().split("：")
                    .let { it[0] to it[1] },
            ),
            note = divs[1].text(),
            pdfUrlString = "$BASE_URL/project/CET/News/TestDataPlan-CET.pdf",
        )
    }

    companion object {
        const val BASE_URL = "https://resource.neea.edu.cn"
    }
}