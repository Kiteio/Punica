package org.kiteio.punica.http

import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 收集所有 Cookie 的 [CookiesStorage] 实现（[参考](https://github.com/ktorio/ktor/blob/main/ktor-client/ktor-client-core/common/src/io/ktor/client/plugins/cookies/AcceptAllCookiesStorage.kt)）。
 */
class CookiesCollector(
    /** Cookie 集合，键为请求 [Url]，值为对应 Cookie 列表。 */
    private val cookies: MutableMap<Url, MutableList<Cookie>> = mutableMapOf(),
) : CookiesStorage {
    /** 互斥锁 */
    private val mutex = Mutex()


    /**
     * 返回 [requestUrl] 对应的所有 Cookie。
     */
    override suspend fun get(requestUrl: Url) =
        mutex.withLock { cookies[requestUrl]?.toList() ?: emptyList() }


    /**
     * 更新 [cookie] 到 [requestUrl] 对应的 Cookie 集合。
     */
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        mutex.withLock {
            cookies.getOrPut(requestUrl) { mutableListOf() }.apply {
                // 移除旧 Cookie
                removeAll { it.name == cookie.name }
                // 添加新 Cookie
                add(cookie)
            }
        }
    }


    /**
     * 关闭 [CookiesCollector]。
     */
    override fun close() = cookies.clear()
}