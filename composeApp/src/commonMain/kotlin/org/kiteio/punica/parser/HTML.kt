package org.kiteio.punica.parser

import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

/**
 * 解析 html
 */
inline fun parseHTML(html: String, block: KsoupHtmlHandler.Builder.() -> Unit) {
    KsoupHtmlParser(handler = KsoupHtmlHandler.Builder().apply(block).build()).apply {
        write(html)
        end()
    }
}