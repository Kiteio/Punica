package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.char
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.foundation.Term

/**
 * 返回学期 [term] 的日历。
 */
suspend fun AcademicSystem.getTermDateRange(term: Term = Term.current): TermDateRange {
    return withContext(Dispatchers.Default) {
        val text = submitForm(
            "jsxsd/jxzl/jxzl_query",
            parameters { append("xnxq01id", "$term") }
        ).bodyAsText()

        val doc = Ksoup.parse(text)
        val tds = doc.getElementsByTag("td")

        // yyyy年MM月DD
        val formatter = LocalDate.Format {
            year(); char('年')
            monthNumber();char('月')
            day()
        }

        val start = LocalDate.parse(
            tds[3].attr("title"),
            formatter,
        )
        val end = LocalDate.parse(
            tds[tds.lastIndex - 3].attr("title"),
            formatter,
        )

        return@withContext TermDateRange(term, start..end)
    }
}


/**
 * 学期日历。
 */
@Serializable
data class TermDateRange(
    val term: Term,
    val range: ClosedRange<LocalDate>,
)