package org.kiteio.punica.http

import io.ktor.client.request.*
import io.ktor.http.*

/**
 * 设置 [cookie]。
 */
fun HttpRequestBuilder.cookie(cookie: Cookie) {
    cookie(
        name = cookie.name,
        value = cookie.value,
        maxAge = cookie.maxAge ?: 0,
        expires = cookie.expires,
        domain = cookie.domain,
        path = cookie.path,
        secure = cookie.secure,
        httpOnly = cookie.httpOnly,
        extensions = cookie.extensions,
    )
}