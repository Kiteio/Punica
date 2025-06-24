package org.kiteio.punica.mirror.platform

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

/**
 * 平台特殊配置的 [HttpClient]。
 */
expect fun platformHttpClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient