package org.kiteio.punica.mirror.platform

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
    language: String = "eng",
    borderWidth: Int = 3,
    charWhiteList: String = "123abcdmnvxz",
): String