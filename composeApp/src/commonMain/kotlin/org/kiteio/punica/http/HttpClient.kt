package org.kiteio.punica.http

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import org.kiteio.punica.serialization.Json

/**
 * 返回 [HttpClient]。
 */
fun HttpClient(
    baseUrl: String,
    cookies: MutableMap<String, MutableList<Cookie>> = mutableMapOf(),
    withDefaultHeader: Boolean = true,
) = HttpClient {
    defaultRequest {
        url(urlString = baseUrl)
        if (withDefaultHeader) {
            header(HttpHeaders.AcceptEncoding, "br")
        }
    }
    // 内容协商：Json
    install(ContentNegotiation) { json(Json) }
    // 超时：4000ms
    install(HttpTimeout) { requestTimeoutMillis = 10000; connectTimeoutMillis = 10000 }
    // Cookie 管理
    install(HttpCookies) { storage = CookiesCollector(cookies) }
}