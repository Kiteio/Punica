package org.kiteio.punica.mirror.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sourceforge.tess4j.ITessAPI
import net.sourceforge.tess4j.Tesseract
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

actual suspend fun ByteArray.readText(
    dataPath: String,
    language: String,
    borderWidth: Int,
    charWhiteList: String,
): String = withContext(Dispatchers.Default) {
    useTesseract(dataPath, language) {
        ByteArrayInputStream(this@readText).use {
            val bufferedImage = ImageIO.read(it)
            // 灰度化处理
            bufferedImage.greyscale(borderWidth)

            // 单行识别
            setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_LINE)
            // 白名单
            setVariable("tessedit_char_whitelist", charWhiteList)

            // 识别
            doOCR(bufferedImage).trim()
        }
    }
}

/**
 * 使用 [Tesseract]，会自动释放。
 *
 * @param dataPath tessdata 目录
 * @param language 语言
 */
private inline fun <R> useTesseract(
    dataPath: String,
    language: String,
    block: Tesseract.() -> R,
): R {
    return with(Tesseract()) {
        setDatapath(dataPath)
        setLanguage(language)
        block()
    }
}


/**
 * [BufferedImage] 灰度化处理。
 */
private fun BufferedImage.greyscale(borderWidth: Int) {
    val pixels = IntArray(width * height)
    getRGB(0, 0, width, height, pixels, 0, width)

    for (index in pixels.indices) {
        val x = index % width
        val y = index / width
        val isBorder = x < borderWidth || x >= width - borderWidth ||
                y < borderWidth || y >= height - borderWidth

        pixels[index] = when {
            // 去除边框
            isBorder -> Color.WHITE.rgb
            // 灰度化
            else -> {
                val pixel = pixels[index]
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF
                val gray = (red + green + blue) / 3
                if (gray < 113) Color.BLACK.rgb else Color.WHITE.rgb
            }
        }
    }

    setRGB(0, 0, width, height, pixels, 0, width)
}