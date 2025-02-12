package org.kiteio.punica.client.academic.foundation

import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.kiteio.punica.serialization.Identifiable

/**
 * 用户。
 *
 * @property id 学号
 * @property name 用户名
 * @property password 门户密码
 * @property secondClassPwd 第二课堂密码
 * @property networkPwd 校园网密码
 * @property cookies Cookie
 */
@Serializable
data class User(
    override val id: Int,
    val name: String,
    val password: String,
    val secondClassPwd: String,
    val networkPwd: String,
    val cookies: MutableMap<String, MutableList<Cookie>>,
) : Identifiable<Int>