package org.kiteio.punica.mirror.service

import com.fleeksoft.ksoup.Ksoup
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.AES
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.header.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.platform.TOTP
import org.kiteio.punica.mirror.util.Json
import kotlin.random.Random

/**
 * WebVpn 服务。
 */
@OptIn(InternalAPI::class)
fun getWebVpnService(): WebVpnService {
    val httpClient = HttpClient {
        defaultRequest {
            url(WebVpnServiceImpl.BASE_URL)
            // 不加会报异常 source exhausted prematurely
            header(HttpHeaders.AcceptEncoding, AcceptEncoding.All)
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(ContentNegotiation) {
            json(Json)
        }
        // 使用此操作和之后的 defaultTransformers
        // 去除 Accept-Charset，避免被 WAF 拦截
        useDefaultTransformers = false
    }.apply {
        defaultTransformers()
    }

    return WebVpnServiceImpl(httpClient)
}

/**
 * WebVpn 服务。
 */
interface WebVpnService {
    /**
     * 登录。
     *
     * @param userId 学号
     * @param password 门户密码
     * @param totp TOTP
     */
    suspend fun login(
        userId: String,
        password: String,
        totp: TOTP,
    )
}

// --------------- 实现 ---------------

private class WebVpnServiceImpl(
    private val httpClient: HttpClient,
) : WebVpnService {
    override suspend fun login(
        userId: String,
        password: String,
        totp: TOTP,
    ) = withContext(Dispatchers.Default) {
        // 获取 Cookie
        httpClient.get(COOKIE_URL)

        val authParams = getAuthParams() ?: return@withContext

        submitForm(userId, password, authParams)

        auth(totp)
    }

    /**
     * 获取校验参数。
     */
    private suspend fun getAuthParams(): AuthParams? {
        val text = httpClient.get(LOGIN_URL).bodyAsText()

        val doc = Ksoup.parse(text)

        // 已登录
        if (doc.title() == "资源导航登录") return null

        val execution = doc.getElementById("execution")!!.attr("value")
        val key = doc.getElementById("pwdEncryptSalt")!!.attr("value")

        return AuthParams(execution, key)
    }

    /**
     * 校验参数。
     */
    private data class AuthParams(
        val execution: String,
        val key: String,
    )

    /**
     * 提交表单
     */
    @OptIn(DelicateCryptographyApi::class)
    private suspend fun submitForm(
        userId: String,
        password: String,
        authParams: AuthParams,
    ) {
        // 加密密码
        val encodedPassword = CryptographyProvider.Default.get(AES.CBC)
            .keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, authParams.key.toByteArray())
            .cipher().encryptWithIv(
                iv = randomString(16).toByteArray(),
                plaintext = "${randomString(64)}${password}".toByteArray(),
            ).encodeBase64()

        val response = httpClient.submitForm(
            LOGIN_URL,
            parameters {
                append("username", userId)
                append("password", encodedPassword)
                append("_eventId", "submit")
                append("cllt", "userNameLogin")
                append("dllt", "generalLogin")
                append("execution", authParams.execution)
            },
        )

        val text = response.bodyAsText()
        val doc = Ksoup.parse(text)

        // 登录失败会返回到登录页面，并提供错误信息
        check(doc.title() != "统一身份认证平台") {
            doc.getElementById("showErrorTip")!!.text()
        }
    }

    /**
     * 身份验证。
     */
    private suspend fun auth(totp: TOTP) {
        val response = httpClient.submitForm(
            AUTH_URL,
            parameters {
                append("service", "$BASE_URL/rump_frontend/loginFromCas/")
                append("reAuthType", "10")
                append("isMultifactor", "true")
                append("otpCode", totp.generate())
            }
        )

        val body = response.body<AuthData>()
        check(body.code == "reAuth_success") { body.message }

        // 这里的 url 参数必须自己手写
        // 因为参数键“amp;service”中的分号不能进行 url 编码
        val text = httpClient.get(
            "$ENTRY_URL?vpn-0&amp;service=https%3A%2F%2Fsec.gdufe.edu.cn%2Frump_frontend%2FloginFromCas%2F",
        ).bodyAsText()

        val doc = Ksoup.parse(text)
        check(doc.title() == "资源导航登录")
    }

    /**
     * 身份校验响应数据。
     *
     * @property code 状态码
     * @property message 消息
     */
    @Serializable
    data class AuthData(
        val code: String,
        @SerialName("msg") val message: String,
    )

    /**
     * 返回指定长度 [length] 的随机字符串。
     */
    private fun randomString(length: Int): String {
        val sequence = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678"
        return (1..length).joinToString("") {
            sequence[Random.nextInt(sequence.length)].toString()
        }
    }

    companion object {
        const val BASE_URL = "https://sec.gdufe.edu.cn"
        private const val COOKIE_URL = "/rump_frontend/login/"
        private const val SEGMENT =
            "LjE5Ni4yMTYuMTY1LjE1My4xNjMuMTUxLjIxMy4xNjYuMTk4LjE2NC45NC4xNjAuMTUxLjIxOS4xNTUuMTUxLjE0Ny4xNTAuMTQ4LjIxNy4xMDAuMTU2LjE2MQ==/authserver"
        private const val LOGIN_URL = "/webvpn/LjIwMy4yMTUuMTY1LjE2MQ==/$SEGMENT/login"
        private const val AUTH_URL =
            "/webvpn/LjIwMy4yMTUuMTY1LjE2MS4xNjM=/$SEGMENT/reAuthCheck/reAuthSubmit.do"
        private const val ENTRY_URL = "/webvpn/LjIwMy4yMTUuMTY1LjE2MS4xNjM=/$SEGMENT/login"
    }
}