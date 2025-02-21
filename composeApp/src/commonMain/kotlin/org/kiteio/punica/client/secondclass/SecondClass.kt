package org.kiteio.punica.client.secondclass

import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.secondclass.foundation.SecondClassBody
import org.kiteio.punica.http.Client
import org.kiteio.punica.http.HttpClientWrapper
import org.kiteio.punica.serialization.Json

/**
 * 第二课堂。
 *
 * @property id 唯一标识
 * @property token 请求头 X-Token
 * @property userId 学号
 */
interface SecondClass : HttpClientWrapper {
    val id: String
    val token: String
    val userId: Long
}


/**
 * 返回 [userId]、[password] 登录的第二课堂客户端。
 */
suspend fun SecondClass(userId: Long, password: String): SecondClass {
    val client = Client("http://2ketang.gdufe.edu.cn")

    val response = client.submitForm(
        "apps/common/login",
        parameters {
            append(
                "para",
                Json.encodeToString(
                    LoginParameter(
                        schoolId = "10018",
                        userId = userId,
                        password = password.ifEmpty { "$userId" }),
                ),
            )
        }
    )
    val body = response.body<LoginBody>()

    require(body.code == 200) { body.msg }

    return object : SecondClass {
        override val httpClient = client.httpClient
        override val id = body.data.id
        override val token = response.headers["X-token"]!!
        override val userId = userId
    }
}


/**
 * 登录参数。
 *
 * @property schoolId 学校
 * @property userId 学号
 * @property password 密码
 */
@Serializable
private data class LoginParameter(
    @SerialName("school") val schoolId: String,
    @SerialName("account") val userId: Long,
    val password: String,
)


/**
 * 登录响应内容。
 */
@Serializable
private data class LoginBody(
    override val code: Int,
    override val msg: String,
    override val data: Data,
) : SecondClassBody<Data>


/**
 * 数据。
 *
 * @property id 第二课堂 id
 */
@Serializable
private data class Data(val id: String)