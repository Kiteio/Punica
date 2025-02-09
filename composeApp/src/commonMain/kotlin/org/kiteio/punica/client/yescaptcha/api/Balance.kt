package org.kiteio.punica.client.yescaptcha.api

import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.yescaptcha.YesCaptcha

/**
 * 返回账户余额。
 *
 * [参阅](https://yescaptcha.atlassian.net/wiki/spaces/YESCAPTCHA/pages/229767/getBalance)。
 */
suspend fun YesCaptcha.getBalance(): Double = submitForm(
    "getBalance",
    parameters { append("clientKey", key) },
).body<BalanceBody>().balance


/**
 * 账户余额响应内容。
 *
 * @property balance 当前账户所剩点数。
 */
@Serializable
private data class BalanceBody(val balance: Double)