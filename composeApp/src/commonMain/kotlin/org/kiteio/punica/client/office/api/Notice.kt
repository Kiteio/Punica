package org.kiteio.punica.client.office.api

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Evaluator
import io.ktor.client.statement.*
import org.kiteio.punica.client.office.AcademicOffice

/**
 * 返回教务通知。
 */
suspend fun AcademicOffice.getNotices(index: Int = 1): List<Notice> {
    val urlString = "/4133/list${index}.psp"
    val text = get(urlString).bodyAsText()

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
                urlString = "https://jwc.gdufe.edu.cn${a.attr("href")}",
            )
        )
    }

    return notices
}


/**
 * 教务通知。
 *
 * @property title 标题
 * @property time 时间
 * @property urlString Url
 */
data class Notice(
    val title: String,
    val time: String,
    val urlString: String,
)