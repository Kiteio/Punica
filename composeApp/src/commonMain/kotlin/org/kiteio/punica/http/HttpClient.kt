package org.kiteio.punica.http

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.kiteio.punica.serialization.Json

/**
 * 创建以 [baseUrl] 为 [HttpClient]
 */
fun HttpClient(baseUrl: String) = HttpClient {
    defaultRequest { url(urlString = baseUrl); header(HttpHeaders.AcceptEncoding, "br") }
    // 内容协商：Json
    install(ContentNegotiation) { json(Json()) }
    // 超时：4000ms
    install(HttpTimeout) { requestTimeoutMillis = 4000; connectTimeoutMillis = 4000 }
    // Cookie 管理
    install(HttpCookies) { storage = CookiesCollector() }
}