package org.kiteio.punica.client.academic

import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import org.kiteio.punica.client.yescaptcha.YesCaptchaClient
import org.kiteio.punica.client.yescaptcha.api.createTask
import org.kiteio.punica.http.Client
import org.kiteio.punica.http.HttpClientWrapper
import org.kiteio.punica.parser.parseHTML

/**
 * [教务系统](http://jwxt.gdufe.edu.cn/jsxsd/)客户端。
 */
interface AcademicAffairsSystemClient : HttpClientWrapper


/**
 * 返回以 [name]、[password] 登录的教务系统客户端。
 */
suspend fun AcademicAffairsSystemClient(name: String, password: String): AcademicAffairsSystemClient {
    val client = Client("http://jwxt.gdufe.edu.cn")

    // 获取并识别验证码
    val base64 = client.get("/jsxsd/verifycode.servlet").readRawBytes().encodeBase64()
    val captcha = YesCaptchaClient().createTask(base64)

    // 发送登录请求
    val text = client.submitForm(
        "/jsxsd/xk/LoginToXkLdap",
        parameters {
            append("USERNAME", name)
            append("PASSWORD", password)
            append("RANDOMCODE", captcha)
        }
    ).bodyAsText()

    // 若返回空文本，则登录成功；否则，解析 HTML 并获取 font 标签中的错误提示
    return if (text.isEmpty()) object : AcademicAffairsSystemClient {
        override val httpClient = client.httpClient
    } else {
        var isFontTag = false
        parseHTML(text) {
            onOpenTagName { isFontTag = it == "font" }
            onText { if (isFontTag) error(it)  }
        }

        // 出现未知异常
        error("未知 HTML")
    }
}