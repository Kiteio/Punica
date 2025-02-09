package org.kiteio.punica.client.yescaptcha

import org.kiteio.punica.http.HttpClient
import org.kiteio.punica.http.HttpClientWrapper
import org.kiteio.punica.yesCaptchaKey

/**
 * [YesCaptcha](https://yescaptcha.com/) 客户端。
 */
interface YesCaptcha: HttpClientWrapper {
    /** 密钥（ClientKey） */
    val key: String
}


/**
 * 返回以 [key] 作为客户端密钥的 YesCaptcha 客户端。
 *
 * [key] 默认值 [yesCaptchaKey] 为开发者自费提供（参阅 .gitignore），不对外公开。
 */
fun YesCaptcha(key: String = yesCaptchaKey): YesCaptcha {
    return object : YesCaptcha {
        override val key = key
        override val httpClient = HttpClient("https://cn.yescaptcha.com")
    }
}