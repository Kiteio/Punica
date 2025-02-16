package org.kiteio.punica.client.cet

import org.kiteio.punica.http.HttpClient
import org.kiteio.punica.http.HttpClientWrapper

/**
 * CET。
 */
interface CET : HttpClientWrapper


/**
 * 返回 CET 客户端。
 */
fun CET() = object : CET {
    override val httpClient = HttpClient("https://resource.neea.edu.cn")
}