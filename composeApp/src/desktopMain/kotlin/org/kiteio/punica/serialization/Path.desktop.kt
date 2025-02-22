package org.kiteio.punica.serialization

import net.harawata.appdirs.AppDirsFactory
import okio.Path.Companion.toPath
import org.kiteio.punica.Build

actual fun fileDir(path: String): okio.Path {
    val userDataDir = AppDirsFactory.getInstance().getUserDataDir(
        Build.appName,
        Build.versionName,
        Build.organization,
    )

    return "$userDataDir/$path".toPath()
}