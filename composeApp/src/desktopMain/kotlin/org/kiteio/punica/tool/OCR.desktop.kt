package org.kiteio.punica.tool

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sourceforge.tess4j.Tesseract
import java.awt.Color
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

actual suspend fun ByteArray.readText(): String = withContext(Dispatchers.Default) {
    val bufferedImage = ImageIO.read(ByteArrayInputStream(this@readText))
    val tesseract = Tesseract()
    tesseract.setDatapath(getDataPath())
    tesseract.setLanguage("eng")

    val border = 3
    val width = bufferedImage.width
    val height = bufferedImage.height

    for (y in 0 until height) {
        for (x in 0 until width) {
            if (x < border || x >= width - border || y < border || y >= height - border) {
                // 去除边框
                bufferedImage.setRGB(x, y, Color.WHITE.rgb)
            } else {
                // 二值化处理
                val pixel = bufferedImage.getRGB(x, y)
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF
                val gray = (red + green + blue) / 3

                // 根据灰度值设置黑白
                val newPixel = if (gray < 113) Color.BLACK.rgb else Color.WHITE.rgb
                bufferedImage.setRGB(x, y, newPixel)
            }
        }
    }

    return@withContext tesseract.doOCR(bufferedImage)
}