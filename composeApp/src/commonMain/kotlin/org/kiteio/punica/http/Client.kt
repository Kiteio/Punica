package org.kiteio.punica.http

import io.ktor.http.*

/**
 * Http 客户端，以 baseUrl 作为默认请求 url。
 */
class Client(
    baseUrl: String,
    cookies: MutableMap<String, MutableList<Cookie>> = mutableMapOf(),
) : HttpClientWrapper {
    override val httpClient = HttpClient(baseUrl, cookies)
}