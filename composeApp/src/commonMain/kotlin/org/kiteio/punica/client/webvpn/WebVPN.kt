package org.kiteio.punica.client.webvpn

import com.fleeksoft.ksoup.Ksoup
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.AES
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.http.Client
import org.kiteio.punica.http.HttpClientWrapper
import kotlin.random.Random

/**
 * WebVPN。
 */
interface WebVPN : HttpClientWrapper


/**
 * 返回 [user] 登录的 [WebVPN]。
 *
 * TODO("TOTP 校验成功后，重定向 Url 与浏览器不同。")
 */
@OptIn(DelicateCryptographyApi::class)
suspend fun WebVPN(user: User, onNeedTOTP: () -> String): WebVPN {
    val client = Client("https://authserver.gdufe.edu.cn", user.cookies)
    val text = client.get("authserver/login").bodyAsText()

    val doc = Ksoup.parse(text)
    val form = doc.getElementById("pwdFromId")!!

    // AES CCB key
    val key = form.child(5).value()
    val execution = form.child(6).value()

    // AES CCB
    val encodedPassword = CryptographyProvider.Default.get(AES.CBC)
        .keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, key.toByteArray())
        .cipher().encryptWithIv(
            iv = randomString(16).toByteArray(),
            plaintext = "${randomString(64)}${user.password}".toByteArray(),
        ).encodeBase64()

    // 登录
    var headers = client.submitForm(
        "authserver/login",
        parameters {
            append("username", user.id)
            append("password", encodedPassword)
            append("_eventId", "submit")
            append("cllt", "userNameLogin")
            append("dllt", "generalLogin")
            append("execution", execution)
        }
    ).headers

    // 跟随重定向
    client.get(headers[HttpHeaders.Location]!!)

    // TOTP
    val body = client.submitForm(
        "authserver/reAuthCheck/reAuthSubmit.do",
        parameters {
            append("reAuthType", "10")
            append("isMultifactor", "true")
            append("otpCode", onNeedTOTP())
        }
    ).body<AuthBody>()

    require(body.code == "reAuth_success") { body.msg }

    // 登录
    headers = client.get("authserver/login") {
        parameter("service", "https://imy.gdufe.edu.cn/shiro-cas")
    }.headers

    // 跟随重定向
    client.get(headers[HttpHeaders.Location]!!)

    return object : WebVPN {
        override val httpClient = client.httpClient
    }
}


/**
 * 验证响应内容。
 *
 * @property code 状态字符串
 * @property msg 状态消息
 */
@Serializable
private data class AuthBody(
    val code: String,
    val msg: String,
)


/**
 * 返回指定长度 [length] 的随机字符串。
 */
private fun randomString(length: Int): String {
    val sequence = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678"
    return (1..length).joinToString("") { sequence[Random.nextInt(sequence.length)].toString() }
}