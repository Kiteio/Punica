package org.kiteio.punica.client.academic

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Evaluator
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.client.yescaptcha.YesCaptcha
import org.kiteio.punica.client.yescaptcha.api.createTask
import org.kiteio.punica.http.Client
import org.kiteio.punica.http.HttpClientWrapper

/**
 * [教务系统](http://jwxt.gdufe.edu.cn/jsxsd/)。
 *
 * @property userId 学号
 */
interface AcademicSystem : HttpClientWrapper {
    val userId: String
}


/**
 * 返回以 [user] 登录的教务系统客户端。
 */
suspend fun AcademicSystem(user: User): AcademicSystem {
    return withContext(Dispatchers.Default) {
        val client = Client("http://jwxt.gdufe.edu.cn", user.cookies)

        // 获取并识别验证码
        val base64 = client.get("jsxsd/verifycode.servlet").readRawBytes().encodeBase64()
        val captcha = YesCaptcha().createTask(base64)

        // 发送登录请求
        val text = client.submitForm(
            "jsxsd/xk/LoginToXkLdap",
            parameters {
                append("USERNAME", user.id)
                append("PASSWORD", user.password)
                append("RANDOMCODE", captcha)
            }
        ).bodyAsText()

        // 若返回空文本，则登录成功；否则，解析 HTML 并获取 font 标签中的错误提示
        return@withContext if (text.isEmpty()) object : AcademicSystem {
            override val httpClient = client.httpClient
            override val userId = user.id
        } else {
            val doc = Ksoup.parse(text)
            val message = doc.selectFirst(Evaluator.Tag("font"))?.text()
            error(message ?: doc.title())
        }
    }
}