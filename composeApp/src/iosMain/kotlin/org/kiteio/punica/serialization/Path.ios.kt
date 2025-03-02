package org.kiteio.punica.serialization

import kotlinx.io.files.SystemPathSeparator

actual fun fileDir(path: String): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )!!

    return "${documentDirectory.path}$SystemPathSeparator$path"
}