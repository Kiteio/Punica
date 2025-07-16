package org.kiteio.punica.mirror.util

import kotlinx.io.files.SystemPathSeparator
import net.harawata.appdirs.AppDirsFactory
import org.kiteio.punica.Build

actual object AppDirs {
    actual fun filesDir(path: String): String {
        val userDataDir = AppDirsFactory.getInstance()
            .getUserDataDir(
                Build.appName,
                Build.versionName,
                Build.organization,
            )

        return "$userDataDir$SystemPathSeparator$path"
    }

    actual fun downloadsDir(path: String): String {
        val downloadsDir = AppDirsFactory.getInstance()
            .getUserDownloadsDir(null, null, null)

        return "$downloadsDir$SystemPathSeparator$path"
    }

    actual fun cacheDir(path: String): String {
        val cacheDir = AppDirsFactory.getInstance()
            .getUserCacheDir(
                Build.appName,
                Build.versionName,
                Build.organization,
            )
        return "$cacheDir$SystemPathSeparator$path"
    }
}