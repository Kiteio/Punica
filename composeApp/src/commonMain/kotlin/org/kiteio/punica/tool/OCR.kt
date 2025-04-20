package org.kiteio.punica.tool

import io.ktor.client.plugins.timeout
import io.ktor.client.statement.readRawBytes
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator
import org.kiteio.punica.Build
import org.kiteio.punica.http.Client
import org.kiteio.punica.serialization.fileDir

/**
 * 识别文字。
 */
expect suspend fun ByteArray.readText(): String


/**
 * 返回数据文件路径（包含 tessdata 目录）。
 */
suspend fun getDataPath(): String {
    val dir = fileDir("tessdata")
    val path = Path("$dir${SystemPathSeparator}eng.traineddata")
    if (!SystemFileSystem.exists(path)) {
        // 获取并写入训练数据
        SystemFileSystem.createDirectories(Path(dir))
        val bytes = Client(Build.officialWebsite)
            .get("/downloads/eng.traineddata") {
                timeout { requestTimeoutMillis = 20000 }
            }.readRawBytes()
        SystemFileSystem.sink(path).buffered().use {
            it.write(bytes)
        }
    }
    return dir
}