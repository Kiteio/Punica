package org.kiteio.punica.mirror.platform

import io.ktor.client.plugins.cache.storage.FileStorage
import java.io.File

actual fun CacheStorage(path: String) = FileStorage(File(path))