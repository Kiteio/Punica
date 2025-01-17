package org.kiteio.punica.http

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.Parameters

/**
 * Ktor [HttpClient] 包装。
 */
interface HttpClientWrapper {
    /** Ktor [HttpClient] */
    val httpClient: HttpClient


    /**
     * 发送 GET 请求，返回响应内容。
     */
    suspend fun get(urlString: String, block: HttpRequestBuilder.() -> Unit) =
        httpClient.get(urlString, block)


    /**
     * 发送 POST 请求，返回响应内容。
     */
    suspend fun post(
        urlString: String,
        formParameters: Parameters = Parameters.Empty,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = httpClient.submitForm(urlString, formParameters, block = block)
}