package org.kiteio.punica.client.academic.foundation

import io.ktor.http.Cookie
import kotlinx.serialization.Serializable

/**
 * 用户。
 *
 * @property id 学号
 * @property password 门户密码
 * @property secondClassPwd 第二课堂密码
 * @property otpSecret OTP 密钥
 * @property cookies Cookie
 */
@Serializable
data class User(
    val id: String,
    val password: String = "",
    val secondClassPwd: String = "",
    val otpSecret: String = "",
    val cookies: MutableMap<String, MutableList<Cookie>> = mutableMapOf(),
)