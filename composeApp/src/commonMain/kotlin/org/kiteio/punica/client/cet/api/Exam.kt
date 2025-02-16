package org.kiteio.punica.client.cet.api

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import io.ktor.client.statement.*
import org.kiteio.punica.client.cet.CET

/**
 * 返回考试安排。
 */
suspend fun CET.getExam(): CETExam {
    val text = get("project/CET/IndexEdu.css").bodyAsText()

    val doc = Ksoup.parse(text)
    val divs = doc.getElementsByClass("main_info_l")

    return CETExam(
        time = divs[0].parseText(),
        note = divs[1].parseText(),
        pdfUrlString = "https://resource.neea.edu.cn/project/CET/News/TestDataPlan-CET.pdf",
    )
}


/**
 * CET 考试。
 *
 * @property time 考试时间
 * @property note 备注
 * @property pdfUrlString PDF 文件 Url
 */
data class CETExam(
    val time: String,
    val note: String,
    val pdfUrlString: String,
)


/**
 * 返回元素解析后文字。
 */
private fun Element.parseText() = buildString {
    appendLine(child(0).text())
    append(child(1).children().joinToString("\n") { it.text() })
}