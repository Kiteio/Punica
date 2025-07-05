package util

import io.ktor.utils.io.readText
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.platform.TOTP

val properties by lazy { readProperties("../local.properties") }

/**
 * 读取 [User]。
 */
fun readUser() = User(
    id = properties["user.id"]!!,
    password = properties["user.password"]!!,
    secondClassPwd = "",
)

/**
 * 读取 [TOTP]。
 */
fun readTotp() = TOTP(properties["totp.secret"]!!)

/**
 * 读取 local.properties
 */
private fun readProperties(path: String): Map<String, String> {
    val properties = hashMapOf<String, String>()
    val text = SystemFileSystem
        .source(Path(path))
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