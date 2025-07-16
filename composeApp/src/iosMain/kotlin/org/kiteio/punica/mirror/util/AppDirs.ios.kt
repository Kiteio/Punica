package org.kiteio.punica.mirror.util

import kotlinx.io.files.SystemPathSeparator
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask

actual object AppDirs {
    actual fun filesDir(path: String): String {
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

    actual fun cacheDir(path: String): String {
        TODO("Not yet implemented")
    }
}