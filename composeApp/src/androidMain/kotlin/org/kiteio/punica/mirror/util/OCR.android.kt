package org.kiteio.punica.mirror.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.files.SystemPathSeparator

actual suspend fun ByteArray.readText(
    dataPath: String,
    language: String,
    borderWidth: Int,
    charWhiteList: String,
): String = withContext(Dispatchers.Default) {
    useTesseract(dataPath, language) {
        // 创建 Bitmap
        val bitmap = BitmapFactory.decodeByteArray(
            this@readText,
            0,
            size,
            BitmapFactory.Options().apply { inMutable = true },
        )

        try {
            // 灰度化处理
            bitmap.greyscale(borderWidth)

            // 单行识别
            pageSegMode = TessBaseAPI.PageSegMode.PSM_SINGLE_LINE
            // 白名单
            setVariable(
                TessBaseAPI.VAR_CHAR_WHITELIST,
                charWhiteList,
            )

            // 识别
            setImage(bitmap)
            utF8Text
        } finally {
            bitmap.recycle()
        }
    }
}

/**
 * 使用 [TessBaseAPI]，会自动释放。
 *
 * @param dataPath tessdata 目录
 * @param language 语言
 */
private inline fun <R> useTesseract(
    dataPath: String,
    language: String,
    block: TessBaseAPI.() -> R,
): R {
    return with(TessBaseAPI()) {
        try {
            // tessdata 的上一级目录
            val dir = dataPath
                .removeSuffix("$SystemPathSeparator")
                .removeSuffix("${SystemPathSeparator}tessdata")
            // 初始化 Tesseract
            require(init(dir, language))
            block()
        } finally {
            recycle()
        }
    }
}

/**
 * [Bitmap] 灰度化处理。
 */
private fun Bitmap.greyscale(borderWidth: Int) {
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)

    for (index in pixels.indices) {
        val x = index % width
        val y = index / width
        val isBorder = x < borderWidth || x >= width - borderWidth ||
                y < borderWidth || y >= height - borderWidth

        pixels[index] = when {
            // 去除边框
            isBorder -> Color.WHITE
            // 灰度化
            Color.luminance(pixels[index]) < 0.443f -> Color.BLACK
            else -> Color.WHITE
        }
    }

    setPixels(pixels, 0, width, 0, 0, width, height)
}