package org.kiteio.punica.mirror.util

import android.content.Context
import android.os.Environment
import kotlinx.io.files.SystemPathSeparator
import org.koin.java.KoinJavaComponent.inject

actual object AppDirs {
    private val context by inject<Context>(Context::class.java)

    actual fun filesDir(path: String): String {
        return "${context.filesDir}$SystemPathSeparator$path"
    }

    actual fun downloadsDir(path: String): String {
        val downloadsDir = Environment
            .getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )

        return "$downloadsDir$SystemPathSeparator$path"
    }

    actual fun cacheDir(path: String): String {
        return "${context.cacheDir}$SystemPathSeparator$path"
    }
}