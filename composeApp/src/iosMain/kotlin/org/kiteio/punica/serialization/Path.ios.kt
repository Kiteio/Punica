package org.kiteio.punica.serialization

import okio.Path
import okio.Path.Companion.toPath

actual fun fileDir(path: String): Path {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )!!

    return "${documentDirectory.path}/$path".toPath()
}