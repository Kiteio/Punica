package org.kiteio.punica.serialization

import android.os.Environment
import kotlinx.io.files.SystemPathSeparator
import org.kiteio.punica.applicationContext

actual fun fileDir(path: String) = "${applicationContext.filesDir}$SystemPathSeparator$path"

actual fun downloadsDir(path: String): String {
    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    return "${downloadDir.absolutePath}$SystemPathSeparator$path"
}