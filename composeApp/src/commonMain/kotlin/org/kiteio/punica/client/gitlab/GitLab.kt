package org.kiteio.punica.client.gitlab

import org.kiteio.punica.http.HttpClient
import org.kiteio.punica.http.HttpClientWrapper

/**
 * GitLab 客户端。
 */
interface GitLab : HttpClientWrapper


/**
 * 返回 GitLab 客户端。
 */
fun GitLab(baseUrl: String = "https://framagit.org") = object : GitLab {
    override val httpClient = HttpClient(baseUrl)
}