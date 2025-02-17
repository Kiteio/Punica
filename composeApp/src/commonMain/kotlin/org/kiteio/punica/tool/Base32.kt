package org.kiteio.punica.tool

/**
 * 返回解码 Base32 后的 [ByteArray]。
 */
fun String.decodeBase32Bytes(): ByteArray {
    val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
    val base32Lookup = IntArray(256).apply {
        for (i in base32Chars.indices) {
            this[base32Chars[i].code] = i
        }
    }

    var buffer = 0
    var shift = 0
    val result = ByteArray((length * 5 + 7) / 8)
    var index = 0

    for (char in uppercase()) {
        if (char == '=') break // 忽略填充字符
        val value = base32Lookup[char.code]
        if (value == 0 && char != 'A') throw IllegalArgumentException("Invalid Base32 character: $char")

        buffer = buffer shl 5 or value
        shift += 5

        if (shift >= 8) {
            shift -= 8
            result[index++] = (buffer shr shift).toByte()
        }
    }

    return result.copyOf(index)
}