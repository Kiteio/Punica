package org.kiteio.punica.mirror.platform

import io.ktor.client.plugins.cache.storage.CacheStorage

/**
 * [CacheStorage]。
 */
expect fun CacheStorage(path: String): CacheStorage