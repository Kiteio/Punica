package org.kiteio.punica.serialization

import kotlinx.io.files.SystemPathSeparator
import net.harawata.appdirs.AppDirsFactory
import org.kiteio.punica.Build

actual fun fileDir(path: String): String {
    val userDataDir = AppDirsFactory.getInstance().getUserDataDir(
        Build.appName,
        Build.versionName,
        Build.organization,
    )

    return "$userDataDir$SystemPathSeparator$path"
}