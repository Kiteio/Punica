package org.kiteio.punica.client.academic

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Evaluator
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import org.kiteio.punica.client.yescaptcha.YesCaptcha
import org.kiteio.punica.client.yescaptcha.api.createTask
import org.kiteio.punica.http.Client
import org.kiteio.punica.http.HttpClientWrapper

/**
 * [教务系统](http://jwxt.gdufe.edu.cn/jsxsd/)客户端。
 */
interface AcademicAffairsSystem : HttpClientWrapper {
    val userId: Long
}


/**
 * 返回以 [userId]（学号）、[password]（门户密码） 登录的教务系统客户端。
 */
suspend fun AcademicAffairsSystem(userId: Long, password: String): AcademicAffairsSystem {
    val client = Client("http://jwxt.gdufe.edu.cn")

    // 获取并识别验证码
    val base64 = client.get("jsxsd/verifycode.servlet").readRawBytes().encodeBase64()
    val captcha = YesCaptcha().createTask(base64)

    // 发送登录请求
    val text = client.submitForm(
        "jsxsd/xk/LoginToXkLdap",
        parameters {
            append("USERNAME", userId.toString())
            append("PASSWORD", password)
            append("RANDOMCODE", captcha)
        }
    ).bodyAsText()

    // 若返回空文本，则登录成功；否则，解析 HTML 并获取 font 标签中的错误提示
    return if (text.isEmpty()) object : AcademicAffairsSystem {
        override val httpClient = client.httpClient
        override val userId = userId
    } else {
        val doc = Ksoup.parse(text)
        val message = doc.selectFirst(Evaluator.Tag("font"))?.text()
        error(message ?: doc.title())
    }
}