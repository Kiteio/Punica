package org.kiteio.punica.client.bing

import org.kiteio.punica.http.HttpClient
import org.kiteio.punica.http.HttpClientWrapper

interface Bing : HttpClientWrapper {
    val baseUrl: String
}


/**
 * 返回 Bing 客户端。
 */
fun Bing() = object : Bing {
    override val baseUrl = "https://cn.bing.com"
    override val httpClient = HttpClient(baseUrl, withDefaultHeader = false)
}