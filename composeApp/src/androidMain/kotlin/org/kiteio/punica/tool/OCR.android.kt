package org.kiteio.punica.tool

import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.Color
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun ByteArray.readText(): String = withContext(Dispatchers.Default) {
    with(TessBaseAPI()) {
        require(init(getDataPath().replace("tessdata", ""), "eng"))

        BitmapFactory.decodeByteArray(
            this@readText,
            0,
            size,
            Options().apply { inMutable = true },
        ).apply {
            val border = 3
            for (y in 0..<height) for (x in 0..<width) {
                if (x < border || x > width - border || y < border || y > height - border)
                // 去除边框
                    setPixel(x, y, Color.WHITE)
                else {
                    // 二值化
                    val pixel = getPixel(x, y)
                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)
                    setPixel(x, y, if ((red + green + blue) / 3 < 113) Color.BLACK else Color.WHITE)
                }
            }
            // 向 TessBaseAPI 设置 Bitmap
            setImage(this@apply)
        }

        return@withContext utF8Text.also { recycle() }
    }
}