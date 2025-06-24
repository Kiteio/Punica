package org.kiteio.punica.mirror.platform

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

actual fun platformHttpClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient = HttpClient(block)