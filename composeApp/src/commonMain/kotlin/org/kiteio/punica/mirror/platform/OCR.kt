package org.kiteio.punica.mirror.platform

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator
import org.kiteio.punica.mirror.util.AppDirs
import punica.composeapp.generated.resources.Res

/**
 * 使用 Tesseract 识别文字。
 *
 * @param dataPath tessdata 目录
 * @param language 识别语言
 * @param borderWidth 图片边框宽度
 * @param charWhiteList 仅的字符
 */
expect suspend fun ByteArray.readText(
    dataPath: String,
    language: String,
    borderWidth: Int,
    charWhiteList: String,
): String

suspend fun ByteArray.readCaptcha(): String {
    val dir = AppDirs.filesDir("tessdata")
    val path = Path("$dir${SystemPathSeparator}eng.traineddata")

    if (!SystemFileSystem.exists(path)) {
        // 将资源文件写入 tessdata 目录
        val bytes = Res.readBytes("files/tessdata/eng.traineddata")

        SystemFileSystem.createDirectories(Path(dir))
        SystemFileSystem.sink(path).buffered().use {
            it.write(bytes)
        }
    }

    return readText(
        dir,
        language = "eng",
        borderWidth = 3,
        charWhiteList = "123abcdmnvxz",
    )
}