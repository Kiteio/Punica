package org.kiteio.punica.tool

import kotlinx.serialization.Serializable

/**
 * Time-Based One-Time Password（TOTP）。
 */
interface TOTP {
    /**
     * 返回 TOTP 密码。
     */
    fun generate(): String
}


/**
 * 返回密钥为 [secret] 的 [TOTP]。
 */
expect fun TOTP(secret: String): TOTP


/**
 * TOTP 用户。
 */
@Serializable
data class TOTPUser(
    val name: String,
    val secret: String,
)