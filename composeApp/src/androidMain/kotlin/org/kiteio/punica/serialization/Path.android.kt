package org.kiteio.punica.serialization

import kotlinx.io.files.SystemPathSeparator
import org.kiteio.punica.applicationContext

actual fun fileDir(path: String) = "${applicationContext.filesDir}$SystemPathSeparator$path"