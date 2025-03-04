package org.kiteio.punica.client.gitee

import org.kiteio.punica.http.HttpClientWrapper

/**
 * Gitee 客户端。
 */
interface Gitee : HttpClientWrapper


/**
 * 返回 Gitee 客户端。
 */
fun Gitee() = object : Gitee {
    override val httpClient = org.kiteio.punica.http.HttpClient("https://gitee.com")
}