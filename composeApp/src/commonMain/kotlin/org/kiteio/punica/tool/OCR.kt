package org.kiteio.punica.tool

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator
import org.kiteio.punica.serialization.fileDir
import punica.composeapp.generated.resources.Res

/**
 * 识别文字。
 */
expect suspend fun ByteArray.readText(): String


/**
 * 返回数据文件路径（包含 tessdata 目录）。
 *
 * Tesseract 需要本地文件系统路径，无法直接读取 Compose 资源，
 * 因此首次使用时从内置资源复制到应用数据目录。
 */
suspend fun getDataPath(): String {
    val dir = fileDir("tessdata")
    val path = Path("$dir${SystemPathSeparator}eng.traineddata")
    if (!SystemFileSystem.exists(path)) {
        SystemFileSystem.createDirectories(Path(dir))
        val bytes = Res.readBytes("files/eng.traineddata")
        SystemFileSystem.sink(path).buffered().use {
            it.write(bytes)
        }
    }
    return dir
}