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

actual fun downloadsDir(path: String): String {
    val fileManager = NSFileManager.defaultManager
    val urls = fileManager.URLsForDirectory(
        directory = NSFileManager.DownloadsDirectory,
        inDomains = NSUserDomainMask
    )
    return "${(urls.firstOrNull() as? NSURL).path}$SystemPathSeparator$path"
}