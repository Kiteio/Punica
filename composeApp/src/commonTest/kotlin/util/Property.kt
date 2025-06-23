package util

import io.ktor.utils.io.readText
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

/**
 * 读取 local.properties
 */
fun readProperties(): Map<String, String> {
    val properties = hashMapOf<String, String>()
    val text = SystemFileSystem
        .source(Path("../local.properties"))
        .buffered()
        .readText()
    text.split("\n").forEach {
        if (!it.startsWith('#') && it.isNotEmpty()) {
            val (key, value) = it.split("=")
            properties[key.trim()] = value.trim()
        }
    }

    return properties
}