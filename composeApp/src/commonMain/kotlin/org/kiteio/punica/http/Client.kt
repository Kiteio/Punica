package org.kiteio.punica.http

/**
 * Http 客户端，以 baseUrl 作为默认请求 url。
 */
class Client(baseUrl: String) : HttpClientWrapper {
    override val httpClient = HttpClient(baseUrl)
}