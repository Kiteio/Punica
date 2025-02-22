package org.kiteio.punica.serialization

import okio.Path.Companion.toPath
import org.kiteio.punica.applicationContext

actual fun fileDir(path: String) = "${applicationContext.filesDir}/$path".toPath()