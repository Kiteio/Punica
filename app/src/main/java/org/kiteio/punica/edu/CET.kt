package org.kiteio.punica.edu

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.candy.API
import org.kiteio.punica.candy.route
import org.kiteio.punica.request.fetch

/**
 * CET 考试
 */
object CET : API {
    override val root = "https://resource.neea.edu.cn"
    private const val TIME = "/project/CET/IndexEdu.css"  // 考试时间
    const val TIME_PDF = "/project/CET/News/TestDataPlan-CET.pdf"


    /**
     * 考试时间
     * @return [CETTime]
     */
    suspend fun time() = withContext(Dispatchers.Default) {
        val text = fetch(route { TIME }) {
            header(HttpHeaders.UserAgent, System.getProperty("http.agent"))
        }.bodyAsText()

        val infos = Ksoup.parse(text).getElementsByClass("main_info_l")[0]
        val lis = infos.child(1).children()

        val rows = arrayListOf<String>()
        for (li in lis) {
            if (li.childrenSize() > 0)
                rows.addAll(li.children().textNodes().map { textNode -> textNode.text() })
            else rows.add(li.text())
        }

        return@withContext CETTime(infos.child(0).text(), rows)
    }
}


/**
 * 考试时间
 * @property name 考试名称
 * @property rows 考试信息
 */
class CETTime(
    val name: String,
    val rows: List<String>
)