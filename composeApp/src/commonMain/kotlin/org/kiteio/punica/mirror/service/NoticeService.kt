package org.kiteio.punica.mirror.service

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Evaluator
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.mirror.modal.notice.Notice

/**
 * 教学通知服务。
 */
@Singleton
fun getNoticeService(): NoticeService {
    val httpClient = HttpClient {
        defaultRequest {
            url(NoticeServiceImpl.BASE_URL)
        }
    }

    return NoticeServiceImpl(httpClient)
}

/**
 * 教学通知服务。
 */
interface NoticeService {
    /**
     * 通知列表，每页 14 项。
     *
     * @param page 页码
     */
    suspend fun getNotices(page: Int = 1): List<Notice>

    /**
     * 通知。
     *
     * @param urlString 通知 url
     */
    suspend fun getNotice(urlString: String): String
}

// --------------- 实现 ---------------

private class NoticeServiceImpl(
    private val httpClient: HttpClient,
) : NoticeService {
    override suspend fun getNotices(
        page: Int,
    ) = withContext(Dispatchers.Default) {
        val text = httpClient.get("/4133/list${page}.psp")
            .bodyAsText()

        val doc = Ksoup.parse(text)
        val ul = doc.selectFirst(Evaluator.Class("news_list list2"))!!
        val lis = ul.children()

        val notices = mutableListOf<Notice>()

        for (li in lis) {
            val a = li.child(0).child(0)
            notices.add(
                Notice(
                    title = a.attr("title"),
                    time = li.child(1).text(),
                    urlString = "$BASE_URL${a.attr("href")}",
                )
            )
        }

        return@withContext notices
    }

    override suspend fun getNotice(urlString: String): String {
        val text = httpClient.get(urlString)
            .bodyAsText()

        val doc = Ksoup.parse(text)

        return doc.getElementById("d-container")!!.html()
    }

    companion object {
        const val BASE_URL = "https://jwc.gdufe.edu.cn"
    }
}