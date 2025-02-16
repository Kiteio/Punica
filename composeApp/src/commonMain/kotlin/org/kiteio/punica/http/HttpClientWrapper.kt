package org.kiteio.punica.http

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

/**
 * Ktor [HttpClient] 包装。
 *
 * @property httpClient Ktor [HttpClient]
 */
interface HttpClientWrapper {
    val httpClient: HttpClient


    /**
     * 返回 GET 请求响应内容。
     */
    suspend fun get(urlString: String, block: HttpRequestBuilder.() -> Unit = {}) =
        httpClient.get(urlString, block)


    /**
     * 返回 POST 请求响应内容。
     */
    suspend fun post(urlString: String, block: HttpRequestBuilder.() -> Unit = {}) =
        httpClient.post(urlString, block)


    /**
     * 返回表单提交响应内容。
     */
    suspend fun submitForm(
        urlString: String,
        formParameters: Parameters = Parameters.Empty,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = httpClient.submitForm(urlString, formParameters, block = block)
}