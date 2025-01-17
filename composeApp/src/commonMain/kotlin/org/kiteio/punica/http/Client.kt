package org.kiteio.punica.http

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import org.kiteio.punica.serialization.Json

/**
 * Http 客户端，以 [baseUrl] 作为默认请求 url。
 */
class Client(baseUrl: String) : HttpClientWrapper {
    override val httpClient = HttpClient {
        defaultRequest { url(urlString = baseUrl); header(HttpHeaders.AcceptEncoding, "br") }
        // 内容协商：Json
        install(ContentNegotiation) { json(Json()) }
        // 超时：4000ms
        install(HttpTimeout) { requestTimeoutMillis = 4000; connectTimeoutMillis = 4000 }
        // Cookie 管理
        install(HttpCookies) { storage = CookiesCollector() }
    }
}